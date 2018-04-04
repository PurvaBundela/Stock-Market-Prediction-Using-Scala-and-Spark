import java.io.File

import breeze.numerics.exp
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.{SQLContext, SparkSession}

import scala.collection.JavaConverters._

object DataClean {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "c:\\winutils\\")
    val sqlContext = SparkSession.builder().master("local").appName("Review").getOrCreate()
    val df = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("sheetName", "all_stocks_5yr") // Required
      .option("useHeader", "true") // Required
     
      //      .option("addColorColumns", "true") // Optional, default: false
      //      .option("startColumn", 0) // Optional, default: 0
      //      .option("endColumn", 99) // Optional, default: Int.MaxValue
      //      .option("timestampFormat", "MM-dd-yyyy HH:mm:ss") // Optional, default: yyyy-mm-dd hh:mm:ss[.fffffffff]
      //      .option("maxRowsInMemory", 20) // Optional, default None. If set, uses a streaming reader which can help with big files
     
      .load("D:\\NEU\\Sem 4\\Scala\\FinalProjectDump\\all_stocks_5yr.csv\\all_stocks_5yr.csv")
    df.printSchema()
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._
    import org.apache.spark.sql._
    val clean = df.withColumn("_c6", $"_c6".cast(IntegerType))
    clean.where($"_c6".isNull).show
    ////    val typeMap = df.map(column =>
    ////      column._2 match {
    ////        case "IntegerType" => (column._1 -> 0)
    ////        case "StringType" => (column._1 -> "AAl")
    ////        case "DoubleType" => (column._1 -> 0.0)
    ////      })
    //    //val newDf = df.na.fill("_c1",Seq("blank"))
    ////    //df.na.fill().show()
    val df2 = df.na.fill("1/1/2000", Seq("_c0"))
      .na.fill("0", Seq("_c1"))
      .na.fill("0", Seq("_c2"))
      .na.fill("0", Seq("_c3"))
      .na.fill("0", Seq("_c4"))
      .na.fill("0", Seq("_c5"))
      .na.fill("AAL", Seq("_c6"))
    df2.write
      .format("com.databricks.spark.csv")
      .option("sheetName", "Daily")
      .option("useHeader", "true")
      //      .option("dateFormat", "yy-mmm-d") // Optional, default: yy-m-d h:mm
      //      .option("timestampFormat", "mm-dd-yyyy hh:mm:ss") // Optional, default: yyyy-mm-dd hh:mm:ss.000
      .mode("overwrite")
      .save("D:\\NEU\\Sem 4\\Scala\\FinalProjectDump\\all_stocks_5yr.csv\\test.csv")
  }
}
