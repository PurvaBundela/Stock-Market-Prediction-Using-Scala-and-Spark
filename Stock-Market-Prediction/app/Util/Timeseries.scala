package Util

import java.io.File
import java.time.{ZoneId, ZonedDateTime}
import java.util

import com.cloudera.sparkts.models.ARIMA
import com.cloudera.sparkts._
import com.cloudera.sparkts.{DateTimeIndex, DayFrequency, TimeSeriesRDD}
import org.apache.spark.ml.{Pipeline, evaluation}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.log4j._
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.sql.functions.to_timestamp
import org.apache.spark.streaming.Time
import com.cloudera.sparkts.DateTimeIndex._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.mllib.linalg.{DenseVector, Vector, Vectors}
import org.joda.time.LocalDate

import scala.language.postfixOps



object Timeseries extends App{


    lazy val conf = {
        new SparkConf(false)
          .setMaster("local[*]")
          .setAppName("Stock-prediction")
          .set("spark.logconf","true")
    }



    override def main(args: Array[String]) ={
        trainAndPredictPrice()
    }



    def trainAndPredictPrice():Array[String]={


        val spark = SparkSession.builder().appName("Stock-prediction").master("local[*]").getOrCreate();
        import spark.implicits._
        val appleDf: DataFrame = spark

            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/AAPL_data_train.csv")

        val apple = appleDf.select(appleDf("date").as("appleDate"), appleDf("close").as("closeApple"))
        val applePriceActual = apple.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val appleMean = applePriceActual.map(_.toDouble).sum/applePriceActual.size
        val appleDf1: DataFrame = spark

            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/AAPL_ToBe.csv")

        val apple1 = appleDf1.select(appleDf1("date").as("appleDate1"), appleDf1("close").as("closeApple1"))
        val applePriceActual1: Array[Double] = apple1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val appleMean1 = applePriceActual1.map(_.toDouble).sum/applePriceActual1.size
        val amazonDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/AMZN_data_train.csv")
        val amazon = amazonDf.select(amazonDf("date").as("amazonDate"), amazonDf("close").as("closeAmazon"))
        val amazonPriceActual = amazon.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val amazonMean = amazonPriceActual.map(_.toDouble).sum/amazonPriceActual.size
        val amazonDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/AMZN_ToBe.csv")
        val amazon1 = amazonDf1.select(amazonDf1("date").as("amazonDate1"), amazonDf1("close").as("closeamazon"))
        val amazonPriceActual1: Array[Double] = amazon1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val amazonMean1 = amazonPriceActual1.map(_.toDouble).sum/amazonPriceActual1.size
        val ebayDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/EBAY_data_train.csv")
        val ebay = ebayDf.select(ebayDf("date").as("ebayDate"), ebayDf("close").as("closeebay"))
        val ebayPriceActual = ebay.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val ebayMean = ebayPriceActual.map(_.toDouble).sum/ebayPriceActual.size
        val ebayDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/EBAY_ToBe.csv")
        val ebay1 = ebayDf1.select(ebayDf1("date").as("ebayDate1"), ebayDf1("close").as("closeebay1"))
        val ebayPriceActual1: Array[Double] = ebay.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val ebayMean1 = ebayPriceActual1.map(_.toDouble).sum/ebayPriceActual1.size
        val expediaDf: DataFrame = spark

            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/EXPE_data_train.csv")
        val expedia = expediaDf.select(expediaDf("date").as("expediaDate"), expediaDf("close").as("closeexpedia"))
        val expediaPriceActual = expedia.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val expediaMean = expediaPriceActual.map(_.toDouble).sum/expediaPriceActual.size
        val expediaDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/EXPE_ToBe.csv")
        val expedia1 = expediaDf1.select(expediaDf1("date").as("expediaDate1"), expediaDf1("close").as("closeexpedia1"))
        val expediaPriceActual1: Array[Double] = expedia1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val expediaMean1 = expediaPriceActual1.map(_.toDouble).sum/expediaPriceActual1.size
        val facebookDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/FB_data_train.csv")
        val facebook = facebookDf.select(facebookDf("date").as("facebookDate"), facebookDf("close").as("closefacebook"))
        val facebookPriceActual = facebook.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val facebookMean = facebookPriceActual.map(_.toDouble).sum/facebookPriceActual.size
        val facebookDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/FB_ToBe.csv")
        val facebook1 = facebookDf1.select(facebookDf1("date").as("facebookDate1"), facebookDf1("close").as("closefacebook1"))
        val facebookPriceActual1: Array[Double] = facebook1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val facebookMean1 = facebookPriceActual1.map(_.toDouble).sum/facebookPriceActual1.size
        val googleDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/GOOGL_data_train.csv")
        val google = googleDf.select(googleDf("date").as("googleDate"), googleDf("close").as("closegoogle"))
        val googlePriceActual = google.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val googleMean = googlePriceActual.map(_.toDouble).sum/googlePriceActual.size
        val googleDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/GOOGL_ToBe.csv")
        val google1 = googleDf1.select(googleDf1("date").as("googleDate1"), googleDf1("close").as("closegoogle1"))
        val googlePriceActual1: Array[Double] = google1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val googleMean1 = googlePriceActual1.map(_.toDouble).sum/googlePriceActual1.size
        val microsoftDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/MSFT_data_train.csv")
        val microsoft = microsoftDf.select(microsoftDf("date").as("microsoftDate"), microsoftDf("close").as("closemicrosoft"))
        val microsoftPriceActual = microsoft.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val microsoftMean = microsoftPriceActual.map(_.toDouble).sum/microsoftPriceActual.size
        val microsoftDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/MSFT_ToBe.csv")
        val microsoft1 = microsoftDf1.select(microsoftDf1("date").as("microsoftDate1"), microsoftDf1("close").as("closemicrosoft1"))
        val microsoftPriceActual1: Array[Double] = microsoft1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val microsoftMean1 = microsoftPriceActual1.map(_.toDouble).sum/microsoftPriceActual1.size
        val tripAdvDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/TRIP_data_train.csv")
        val tripAdv = tripAdvDf.select(tripAdvDf("date").as("tripAdvDate"), tripAdvDf("close").as("closetripAdv"))
        val tripAdvPriceActual = tripAdv.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val tripAdvMean = tripAdvPriceActual.map(_.toDouble).sum/tripAdvPriceActual.size
        val tripAdvDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/TRIP_ToBe.csv")
        val tripAdv1 = tripAdvDf1.select(tripAdvDf1("date").as("tripAdvDate1"), tripAdvDf1("close").as("closetripAdv1"))
        val tripAdvPriceActual1: Array[Double] = tripAdv1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val tripAdvMean1 = tripAdvPriceActual1.map(_.toDouble).sum/tripAdvPriceActual1.size
        val walmartDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/WMT_data_train.csv")
        val walmart = walmartDf.select(walmartDf("date").as("walmartDate"), walmartDf("close").as("closewalmart"))
        val walmartPriceActual: Array[Double] = walmart.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val walmartMean = walmartPriceActual.map(_.toDouble).sum/walmartPriceActual.size
        val walmartDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("../Stock-Market-Prediction/app/WMT_ToBe.csv")
        val walmart1 = walmartDf1.select(walmartDf1("date").as("walmartDate1"), walmartDf1("close").as("closewalmart1"))
        val walmartPriceActual1: Array[Double] = walmart1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val walmartMean1 = walmartPriceActual1.map(_.toDouble).sum/walmartPriceActual1.size
        val data = apple
          .join(amazon, $"appleDate" === $"amazonDate").select($"appleDate", $"closeApple", $"closeAmazon")
          .join(ebay, $"appleDate" === $"ebayDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay")
          .join(expedia, $"appleDate" === $"expediaDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia")
          .join(facebook, $"appleDate" === $"facebookDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook")
          .join(google, $"appleDate" === $"googleDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle")
          .join(microsoft, $"appleDate" === $"microsoftDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle", $"closemicrosoft")
          .join(tripAdv, $"appleDate" === $"tripAdvDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle", $"closemicrosoft", $"closetripAdv")
          .join(walmart, $"appleDate" === $"walmartDate").select($"appleDate".as("date"), $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle", $"closemicrosoft", $"closetripAdv", $"closewalmart")
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
            val arimaModel = ARIMA.fitModel(1, 0, 0, newVec)

            val forecasted = arimaModel.forecast(newVec, noOfDays)
            new org.apache.spark.mllib.linalg.DenseVector(forecasted.toArray.slice(forecasted.size-(noOfDays+1), forecasted.size-1))
        }}
        val companyList:List[String] = df.collectAsTimeSeries().keys.toList

        //Since there are 30 values for each company, So to map those values to each company,->
        //-> duplicating values of each companies to 30 rows.
        val multipleCompanyValues = createMultipleCompanyValues(noOfDays,companyList)

        val priceList = df.collectAsTimeSeries().data.values
        //val priceForecast1: Array[(String, Vector)] = Array.empty[(String,org.apache.spark.mllib.linalg.Vector)]
        val priceForecast = df.collect()

        //Create an array of actual values of stock prices of all companies
        val actualPrices =Array(applePriceActual1,walmartPriceActual1,amazonPriceActual1,expediaPriceActual1,tripAdvPriceActual1,googlePriceActual1,microsoftPriceActual1,facebookPriceActual1,ebayPriceActual1)

        calculateRMSE(actualPrices, priceForecast, 8)
        calculateAccuracy(actualPrices, priceForecast, 8)

        //To save the csv with predicted values of company
        saveCompanyPredictionValues(multipleCompanyValues, priceList)

        //Get most profitable companies
        getTopThreeProfitableCompanies(priceForecast)


//        val aaa = df.toDF("symbol","values")
        val abc = df.collect().map(_._1)

        abc
    }

    def createMultipleCompanyValues[String](n: Int, l: List[String]):List[String] = {
        l flatMap {e => List.fill(n)(e) }
    }

    //Get the companies based on their profits for last 30 days
    def getTopThreeProfitableCompanies(priceForecast: Array[(String, Vector)]):Array[(Double, String)]={

        //Convert the data for multiple days to its profit based on last and first values
        val priceDiff = priceForecast.map(x => x._2).map(x=>x(x.size-1)-x(0))
        val stockName = priceForecast.map(x=>x._1)
        val test = (priceDiff,stockName).zipped.toArray.sortWith (_._1 > _._1)
        for (i <- 0 until test.length){
            println(test(i)._2+" "+test(i)._1)
        }

        //Return tuple of companies and its profit for each stock

        val schema = StructType(
            StructField("Names", StringType, false) ::
              StructField("Profit", DoubleType, false) :: Nil)

        //Create RDD
        val sc = SparkContext.getOrCreate(conf)
        val sqlContext = new SQLContext(sc)
        val rdd = sc.parallelize (test).map (x => Row(x._2, x._1.asInstanceOf[Number].doubleValue()))


        //Create the dataframe from RDD and convert the data to CSV
        val df1 = sqlContext.createDataFrame(rdd, schema).coalesce(1).write.format("com.databricks.spark.csv").save("profit")

        test
    }

    //Save the predicted data to CSV
    def saveCompanyPredictionValues(name:List[String], price: Array[Double]): Unit ={

        //Convert Name and predicted price to tuple
        val zip = (name,price).zipped.toArray

        def dayIterator(start: LocalDate, end: LocalDate) = Iterator.iterate(start)(_ plusDays 1) takeWhile (_ isBefore end)
        val dates:List[LocalDate] = dayIterator(new LocalDate("2018-01-01"), new LocalDate("2018-01-31")).toList
        //Create spark conf to convert the data to dataframe
        val dates1 = dates flatMap {e => List.fill(9)(e) }

        val schema = StructType(
            StructField("Names", StringType, false) ::
              StructField("Price", DoubleType, false) :: Nil)

        //Create RDD



        val sc = SparkContext.getOrCreate(conf)

        val sqlContext = new SQLContext(sc)
        val rdd = sc.parallelize (zip).map (x => Row(x._1, x._2.asInstanceOf[Number].doubleValue()))

        //Create the dataframe from RDD and convert the data to CSV
        val df1 = sqlContext.createDataFrame(rdd, schema).coalesce(1).write.partitionBy("Names").format("com.databricks.spark.csv").save("CompanyPredict")


    }

    def calculateRMSE(actualPrice: Array[Array[Double]],priceForecast: Array[(String, Vector)], noOfDays: Int ):Unit={


    }

    def calculateAccuracy(actualPrice: Array[Array[Double]],priceForecast: Array[(String, Vector)], noOfDays: Int ):Unit={
//        val priceForecast1 = priceForecast.map(_._2)
//        var totalErrorSquare1 = 0.0
//        for (/*j <- 0 until actualPrice.size; */i <- 0 until 29) {
//            println(actualPrice(0)(0))
//            val errorSquare = Math.abs(priceForecast1(j)(i) - actualPrice(j)(i))/actualPrice(j)(i)
//            println(priceForecast1(j)(i) + "\t should be \t" + actualPrice(j)(i) + "\t Error Square = " + errorSquare)
//            totalErrorSquare1 += errorSquare
//        }
//        val accuracy = totalErrorSquare1/(30)
//        println("Accuracy: " + (accuracy * 100) + "%")

        val priceForecast1 = priceForecast.map(_._2)
        var totalErrorSquare = 0.0
        var totalErrorSquare1 = 0.0
        for (j <- 0 until actualPrice.size; i <- 0 until noOfDays) {
            val errorSquare = Math.pow(priceForecast1(j)(i) - actualPrice(j)(i), 2)
            val errorSquare1 = Math.abs(priceForecast1(j)(i) - actualPrice(j)(i))/actualPrice(j)(i)
            println(priceForecast1(j)(i) + "\t should be \t" + actualPrice(j)(i) + "\t Error Square = " + errorSquare)
            totalErrorSquare += errorSquare
            totalErrorSquare1 += errorSquare1
        }

        println("Root Mean Square Error: " + (Math.sqrt(totalErrorSquare/(noOfDays*9))))
        println("Accuracy: " + (totalErrorSquare1/(noOfDays*9)) * 1000 + "%")
        println("Hello")

    }

//    def calculateAccuracy():Unit={
//        //df.registerTempTable("data")
//        //df.collect.foreach(println)
//        val priceForecast = df.collect().map(_._2)
//
//        //val priceForecast:Array[Double] = (df.select("values").rdd.map(r => r(0).asInstanceOf[Double])).collect()
//        //val priceForecast = Array(94.72696689187683,92.84354859087416,92.13168005225876,91.86261777543973,91.76092131762485,91.7224834895712,91.70795528812516,91.70246411841178,91.70038864162733,91.69960418146535)
//        //val priceForecast = priceForecast1.map(_.)
//        //        var totalErrorSquare = 0.0
//        //        for (i <- 0 until priceForecast(0).size) {
//        //            val errorSquare = Math.pow(priceForecast(0)(i) - priceActual(i), 2)
//        //            println(priceForecast(0)(i) + "\t should be \t" + priceActual(i) + "\t Error Square = " + errorSquare)
//        //            totalErrorSquare += errorSquare
//        //        }
//        //        println("Root Mean Square Error: " + Math.sqrt(totalErrorSquare/10))
//
//        //        var totalErrorSquare2 = 0.0
//        //        for (i <- (priceForecast.size - 10) until priceForecast.size) {
//        //            val errorSquare = Math.pow(priceForecast(i) - mean1, 2)
//        //            totalErrorSquare2 += errorSquare
//        //        }
//        //
//        //        var totalErrorSquare3 = 0.0
//        //        for (i <- (priceForecast.size - 10) until priceForecast.size) {
//        //            val errorSquare = Math.pow(priceForecast(i) - priceActual(i), 2)
//        //            //println(priceForecast(i) + "\t should be \t" + priceActual(i) + "\t Error Square = " + errorSquare)
//        //            totalErrorSquare3 += errorSquare
//        //        }
//        //
//        //        val r2 = 1-(totalErrorSquare3/totalErrorSquare2)
//        //        println("R2"+r2)
//
//        var totalErrorSquare1 = 0.0
//        for (i <- 0 until priceForecast(0).size) {
//        val errorSquare = Math.abs(priceForecast(0)(i) - priceActual(i))/priceActual(i)
//        //println(priceForecast(i) + "\t should be \t" + priceActual(i) + "\t Error Square = " + errorSquare)
//        totalErrorSquare1 += errorSquare
//    }
//        println("MAPE: " + Math.sqrt(totalErrorSquare1/10))
//    }
//}
//    }
}
