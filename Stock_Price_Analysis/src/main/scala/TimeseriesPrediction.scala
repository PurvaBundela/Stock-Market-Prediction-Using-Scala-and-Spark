import java.time.{ZoneId, ZonedDateTime}

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
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.sql.functions.to_timestamp
import org.apache.spark.streaming.Time
import com.cloudera.sparkts.DateTimeIndex._
import org.apache.spark.mllib.linalg.DenseVector



object Timeseries {
    def main(args: Array[String]) ={
        val spark = SparkSession.builder().appName("untitled").master("local[*]").getOrCreate();
        import spark.implicits._
        val amaznDf = spark
            .read
            .option("header", "true")
            .csv("A_data.csv")
        val amazn = amaznDf.select(amaznDf("date").as("amznDate"), amaznDf("close").as("closeAmazn"))
        val googDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("AAL_data.csv")
        val goog = googDf.select(googDf("date").as("googDate"), googDf("close").as("closeGoog"))
        val yhooDf: DataFrame = spark
            .read
            .option("header", "true")
            .csv("AAP_data_new (1).csv")
        val yhoo = yhooDf.select(yhooDf("date").as("yhooDate"), yhooDf("close").as("closeYhoo"))
        val priceActual1 = yhoo.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val mean = priceActual1.map(_.toDouble).sum/priceActual1.size
        val yhooDf1: DataFrame = spark
            .read
            .option("header", "true")
            .csv("AAP_data.csv")
        val yhoo1 = yhooDf1.select(yhooDf1("date").as("yhooDate1"), yhooDf1("close").as("closeYhoo1"))
        val priceActual = yhoo1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))
        val mean1 = priceActual.map(_.toDouble).sum/priceActual.size
        val data = amazn
            .join(goog, $"amznDate" === $"googDate").select($"amznDate", $"closeAmazn", $"closeGoog")
            .join(yhoo, $"amznDate" === $"yhooDate").select($"amznDate".as("date"), $"closeAmazn", $"closeGoog", $"closeYhoo")
        val formattedData = data
            .flatMap{
                row =>
                  Array(
                      (row.getString(row.fieldIndex("date")), "amzn", row.getString(row.fieldIndex("closeAmazn"))),
                   (row.getString(row.fieldIndex("date")), "goog", row.getString(row.fieldIndex("closeGoog"))),
                   (row.getString(row.fieldIndex("date")), "yhoo", row.getString(row.fieldIndex("closeYhoo")))
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

        val df = tsRdd.mapSeries{vector => {
                val newVec = new org.apache.spark.mllib.linalg.DenseVector(vector.toArray.map(x => if(x.equals(Double.NaN)) 0 else x))
                val arimaModel = ARIMA.fitModel(1, 0, 0, newVec)
                val forecasted = arimaModel.forecast(newVec, 10)

                new org.apache.spark.mllib.linalg.DenseVector(forecasted.toArray.slice(forecasted.size-(10+1), forecasted.size-1))
               }}//.toDF("symbol","values")
        //df.registerTempTable("data")
        //df.collect.foreach(println)
        val priceForecast = df.collect().map(_._2)

        //val priceForecast:Array[Double] = (df.select("values").rdd.map(r => r(0).asInstanceOf[Double])).collect()
        //val priceForecast = Array(94.72696689187683,92.84354859087416,92.13168005225876,91.86261777543973,91.76092131762485,91.7224834895712,91.70795528812516,91.70246411841178,91.70038864162733,91.69960418146535)
        //val priceForecast = priceForecast1.map(_.)
        var totalErrorSquare = 0.0
        for (i <- 0 until priceForecast(0).size) {
            val errorSquare = Math.pow(priceForecast(0)(i) - priceActual(i), 2)
            println(priceForecast(0)(i) + "\t should be \t" + priceActual(i) + "\t Error Square = " + errorSquare)
            totalErrorSquare += errorSquare
        }
        println("Root Mean Square Error: " + Math.sqrt(totalErrorSquare/10))

//        var totalErrorSquare2 = 0.0
//        for (i <- (priceForecast.size - 10) until priceForecast.size) {
//            val errorSquare = Math.pow(priceForecast(i) - mean1, 2)
//            totalErrorSquare2 += errorSquare
//        }
//
//        var totalErrorSquare3 = 0.0
//        for (i <- (priceForecast.size - 10) until priceForecast.size) {
//            val errorSquare = Math.pow(priceForecast(i) - priceActual(i), 2)
//            //println(priceForecast(i) + "\t should be \t" + priceActual(i) + "\t Error Square = " + errorSquare)
//            totalErrorSquare3 += errorSquare
//        }
//
//        val r2 = 1-(totalErrorSquare3/totalErrorSquare2)
//        println("R2"+r2)

//        var totalErrorSquare1 = 0.0
//        for (i <- (priceForecast.size - 10) until priceForecast.size) {
//            val errorSquare = Math.abs(priceForecast(0)(i) - priceActual(i))/priceActual(i)
//            //println(priceForecast(i) + "\t should be \t" + priceActual(i) + "\t Error Square = " + errorSquare)
//            totalErrorSquare1 += errorSquare
//        }
//        println("MAPE: " + Math.sqrt(totalErrorSquare1/10))
    }
}
