package Util

import java.time.{ZoneId, ZonedDateTime}
import com.cloudera.sparkts.models.ARIMA
import com.cloudera.sparkts.{DateTimeIndex, DayFrequency, TimeSeriesRDD}
import org.apache.spark.sql._
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.to_timestamp
import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.mllib.linalg.{Vector}
import services.SparkTsService.{getData}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps



object Timeseries extends App{

    override def main(args: Array[String]) ={
        trainAndPredictPrice()
    }

    
    //Create spark conf
    lazy val conf = {
        new SparkConf(false).setMaster("local[*]").setAppName("Stock-prediction").set("spark.logconf","true")
    }

    //Create spark session only when required
    lazy val spark = SparkSession.builder().appName("Stock-prediction").master("local[*]").getOrCreate();

    def trainAndPredictPrice():Array[String]={

        //Combine all companies data into single frame
        import spark.implicits._

        //Create an array of actual values of stock prices of all companies
        val actualPrices = getActualPrice()

        val data = getData()
        val formattedData = data
          .flatMap{
              row =>
                  Array(
                      (row.getString(row.fieldIndex("date")), "apple", row.getString(row.fieldIndex("closeApple"))),
                      (row.getString(row.fieldIndex("date")), "amazon", row.getString(row.fieldIndex("closeAmazon"))),
                      (row.getString(row.fieldIndex("date")), "ebay", row.getString(row.fieldIndex("closeebay"))),
                      (row.getString(row.fieldIndex("date")), "expedia", row.getString(row.fieldIndex("closeexpedia"))),
                      (row.getString(row.fieldIndex("date")), "facebook", row.getString(row.fieldIndex("closefacebook"))),
                      (row.getString(row.fieldIndex("date")), "google", row.getString(row.fieldIndex("closegoogle"))),
                      (row.getString(row.fieldIndex("date")), "microsoft", row.getString(row.fieldIndex("closemicrosoft"))),
                      (row.getString(row.fieldIndex("date")), "tripAdvisor", row.getString(row.fieldIndex("closetripAdv"))),
                      (row.getString(row.fieldIndex("date")), "walmart", row.getString(row.fieldIndex("closewalmart")))
                  )
          }.toDF("date","symbol","closingPrice")

        //Convert the dataframe into the format required by the ARIMA model
        val finalDf = formattedData
          .withColumn("timestamp",to_timestamp(formattedData("date")))
          .withColumn("price", formattedData("closingPrice").cast(DoubleType))
          .drop("date","closingPrice").sort("timestamp")
        finalDf.registerTempTable("preData")

        val minDate = finalDf.selectExpr("min(timestamp)").collect()(0).getTimestamp(0)
        val maxDate = finalDf.selectExpr("max(timestamp)").collect()(0).getTimestamp(0)
        val zone = ZoneId.systemDefault()
        val dtIndex = DateTimeIndex.uniformFromInterval(
            ZonedDateTime.of(minDate.toLocalDateTime, zone), ZonedDateTime.of(maxDate.toLocalDateTime, zone), new DayFrequency(1)
        )
        val tsRdd = TimeSeriesRDD.timeSeriesRDDFromObservations(dtIndex, finalDf, "timestamp", "symbol", "price")
        val noOfDays = 30
        val df = tsRdd.mapSeries{vector => {
            val newVec = new org.apache.spark.mllib.linalg.DenseVector(vector.toArray.map(x => if(x.equals(Double.NaN)) 0 else x))

            //Train the values
            val arimaModel = ARIMA.fitModel(1, 0, 0, newVec)

            //Get the forecasted value and convert into the dense vector
            val forecasted = arimaModel.forecast(newVec, noOfDays)

            new org.apache.spark.mllib.linalg.DenseVector(forecasted.toArray.slice(forecasted.size-(noOfDays+1), forecasted.size-1))
        }}

        //Get the list of company names
        val companyList:List[String] = df.collectAsTimeSeries().keys.toList

        //Since there are 30 values for each company, So to map those values to each company,->
        //-> duplicating values of each companies to 30 rows.
        val multipleCompanyValues = createMultipleCompanyValues(noOfDays,companyList)

        //Get the list of array of predicted prices for all 9 companies
        val priceList = df.collectAsTimeSeries().data.values

        //Collect the dataframe in val
        val priceForecast = df.collect()

        calculateRMSE(actualPrices, priceForecast, 8)

        //To save the csv with predicted values of company
        saveCompanyPredictionValues(multipleCompanyValues, priceList)

        //Get most profitable companies
        getTopThreeProfitableCompanies(priceForecast)

        //return list of companies present in the model
        val companies = df.collect().map(_._1)

        //Calculate accuracy and RMSE
        calculateAccuracy(actualPrices, priceForecast, 30)

        companies
    }

    //Get multiple rows of a single company to match the number of forecast
    def createMultipleCompanyValues[String](n: Int, l: List[String]):List[String] = {
        l flatMap {e => List.fill(n)(e) }
    }

    //Get the companies based on their profits for last 30 days
    def getTopThreeProfitableCompanies(priceForecast: Array[(String, Vector)]):Array[(Double, String)]={

        val priceDiff = priceForecast.map(x => x._2).map(x=>x(x.size-1)-x(0))
        val stockName = priceForecast.map(x=>x._1)
        val test = (priceDiff,stockName).zipped.toArray.sortWith (_._1 > _._1)
        for (i <- 0 until test.length){
            println(test(i)._2+" "+test(i)._1)
        }

        //Create schema for creating a dataframe
        val schema = StructType(
            StructField("Names", StringType, false) ::
              StructField("Profit", DoubleType, false) :: Nil)

        //Create RDD
        val sc = SparkContext.getOrCreate(conf)

        val sqlContext = new SQLContext(sc)
        val rdd = sc.parallelize (test).map (x => Row(x._2, x._1.asInstanceOf[Number].doubleValue()))


        //Create the dataframe from RDD and convert the data to CSV
        val df1 = sqlContext.createDataFrame(rdd, schema).coalesce(1).write.format("com.databricks.spark.csv").mode(SaveMode.Overwrite).save("profit")

        test
    }

    //Save the predicted data to CSV
    def saveCompanyPredictionValues(name:List[String], price: Array[Double]): Unit ={

        //Convert Name and predicted price to tuple
        val zip = (name,price).zipped.toArray

        val schema = StructType(
            StructField("Names", StringType, false) ::
              StructField("Price", DoubleType, false) :: Nil)

        //Create RDD
        val sc = SparkContext.getOrCreate(conf)

        val sqlContext = new SQLContext(sc)
        val rdd = sc.parallelize (zip).map (x => Row(x._1, x._2.asInstanceOf[Number].doubleValue()))

        //Create the dataframe from RDD and convert the data of predicted value for all companies to CSV
        val df1 = sqlContext.createDataFrame(rdd, schema).coalesce(1).write.partitionBy("Names").format("com.databricks.spark.csv").mode(SaveMode.Overwrite).save("CompanyPredict")


    }

    def calculateRMSE(actualPrice: Array[Array[Double]],priceForecast: Array[(String, Vector)], noOfDays: Int ):Unit={

        val priceForecast1 = priceForecast.map(_._2)
        val totalErrorSquare: ListBuffer[Double] = ListBuffer()
        val accuracy: ListBuffer[Double] = ListBuffer()
        for (j <- 0 until actualPrice.length; i <- 0 until noOfDays) {
            val errorSquare = Math.pow(priceForecast1(j)(i) - actualPrice(j)(i), 2)
            println(priceForecast1(j)(i) + "\t should be \t" + actualPrice(j)(i) + "\t Error Square = " + errorSquare)
            totalErrorSquare += errorSquare

        }

        println("Root Mean Square Error: " + (Math.sqrt(totalErrorSquare.sum/(noOfDays*9))))


    }

    //Calculate Accuracy and RMSE values
    def calculateAccuracy(actualPrice: Array[Array[Double]],priceForecast: Array[(String, Vector)], noOfDays: Int ):Unit={

        val priceForecast1 = priceForecast.map(_._2)

        val accuracy: ListBuffer[Double] = ListBuffer()
        for (j <- 0 until actualPrice.length; i <- 0 until noOfDays) {

            val errorSquare1 = Math.abs(priceForecast1(j)(i) - actualPrice(j)(i))/actualPrice(j)(i)
            println(priceForecast1(j)(i) + "\t should be \t" + actualPrice(j)(i) + "\t Error Square = " + errorSquare1)

            accuracy += errorSquare1
        }

        println("Accuracy: " + (100-((accuracy.sum/(noOfDays*9)) * 1000)) + "%")

    }

    //Get the actual price for 30 days to compare with the forecasted values of all companies
    def getActualPrice(): Array[Array[Double]] = {

        val appleDf1: DataFrame = createDataFrame("AAPL_ToBe")
        val apple1 = appleDf1.select(appleDf1("date").as("appleDate1"), appleDf1("close").as("closeApple1"))
        val applePriceActual1: Array[Double] = apple1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val amazonDf1: DataFrame = createDataFrame("AMZN_ToBe")
        val amazon1 = amazonDf1.select(amazonDf1("date").as("amazonDate1"), amazonDf1("close").as("closeamazon"))
        val amazonPriceActual1: Array[Double] = amazon1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val ebayDf1: DataFrame = createDataFrame("EBAY_ToBe")
        val ebay1 = ebayDf1.select(ebayDf1("date").as("ebayDate1"), ebayDf1("close").as("closeebay1"))
        val ebayPriceActual1: Array[Double] = ebay1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val expediaDf1: DataFrame = createDataFrame("EXPE_ToBe")
        val expedia1 = expediaDf1.select(expediaDf1("date").as("expediaDate1"), expediaDf1("close").as("closeexpedia1"))
        val expediaPriceActual1: Array[Double] = expedia1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val facebookDf1: DataFrame = createDataFrame("FB_ToBe")
        val facebook1 = facebookDf1.select(facebookDf1("date").as("facebookDate1"), facebookDf1("close").as("closefacebook1"))
        val facebookPriceActual1: Array[Double] = facebook1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val googleDf1: DataFrame = createDataFrame("GOOGL_ToBe")
        val google1 = googleDf1.select(googleDf1("date").as("googleDate1"), googleDf1("close").as("closegoogle1"))
        val googlePriceActual1: Array[Double] = google1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val microsoftDf1: DataFrame = createDataFrame("MSFT_ToBe")
        val microsoft1 = microsoftDf1.select(microsoftDf1("date").as("microsoftDate1"), microsoftDf1("close").as("closemicrosoft1"))
        val microsoftPriceActual1: Array[Double] = microsoft1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val tripAdvDf1: DataFrame = createDataFrame("TRIP_ToBe")
        val tripAdv1 = tripAdvDf1.select(tripAdvDf1("date").as("tripAdvDate1"), tripAdvDf1("close").as("closetripAdv1"))
        val tripAdvPriceActual1: Array[Double] = tripAdv1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val walmartDf1: DataFrame = createDataFrame("WMT_ToBe")
        val walmart1 = walmartDf1.select(walmartDf1("date").as("walmartDate1"), walmartDf1("close").as("closewalmart1"))
        val walmartPriceActual1: Array[Double] = walmart1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val actualPrices =Array(applePriceActual1,walmartPriceActual1,amazonPriceActual1,expediaPriceActual1,tripAdvPriceActual1,googlePriceActual1,microsoftPriceActual1,facebookPriceActual1,ebayPriceActual1)

        actualPrices
    }

    //To create dataframe based on CSV
    def createDataFrame(name: String):DataFrame = {
        return spark.read.option("header", "true").csv(s"../Stock-Market-Prediction/public/$name.csv")
    }

}
