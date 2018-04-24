package Util

import Util.Timeseries.conf
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import com.github.nscala_time.time.Imports._
import com.github.nscala_time.time._
import org.apache.spark.SparkContext
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}




object TwitterMain {

  val ConsumerKey = "zckox0ybm1UheRy7DdirLYB18"
  val ConsumerSecret = "Nx0oDdxfZrRFepfbuMLFqMRvjAhYduaHPwlRCW5r0oHFypwmVn"
  val AccessToken = "988311065896374272-ECPkJhP3GYbJ38RTUDMDNQJuhmeiAWg"
  val AccessSecret = "LuL3oMyVNDpMpVTEmjokScshuoR5ZoozHB42fkcJsS81m"




  def main(args: Array[String]) {

    val aapleScore = CalcAvgScore.calcSentiment("Apple Inc stock")
    val amazonScore = CalcAvgScore.calcSentiment("Amazon.com Inc stock")
    val  ebayScore = CalcAvgScore.calcSentiment("eBay Inc stock")
    val  walmartScore = CalcAvgScore.calcSentiment("Walmart Inc stock")
    val  tripAdvisorScore = CalcAvgScore.calcSentiment("Tripadvisor Inc stock")
    val  googleScore = CalcAvgScore.calcSentiment("Alphabet Inc stock")
    val  facebookScore = CalcAvgScore.calcSentiment("Facebook Inc stock")
    val  microsoftScore = CalcAvgScore.calcSentiment("Microsoft Corporation stock")
    val  expediaScore = CalcAvgScore.calcSentiment(" Expedia Group Inc stock")

    println("1"+ aapleScore)
    println("2"+ amazonScore)
    println("3"+ ebayScore)
    println("4"+ walmartScore)
    println("5"+ tripAdvisorScore)
    println("6"+ googleScore)
    println("7"+ facebookScore)
    println("8"+ microsoftScore)
    println("9"+ expediaScore)

    val list = List(aapleScore,amazonScore,ebayScore,walmartScore,tripAdvisorScore,googleScore,facebookScore,microsoftScore,expediaScore)
    val list1 = List("Apple","Amazon","eBay","Walmart","TripAdvisor","Google","Facebook","Microsoft","Expedia")
    val res = (list,list1).zipped.toArray.sortWith(_._1>_._1)
    for(i <- 0 until res.length){
      println(res(i)._2 + " " + res(i)._1)
    }
    val schema = StructType(
      StructField("name", StringType, false) ::
        StructField("value", DoubleType, false) :: Nil)

    //Create RDD
    val sc = SparkContext.getOrCreate(conf)
    val sqlContext = new SQLContext(sc)
    val rdd = sc.parallelize (res).map (x => Row(x._2, x._1.asInstanceOf[Number].doubleValue()))


    //Create the dataframe from RDD and convert the data to CSV
    val df1 = sqlContext.createDataFrame(rdd, schema).coalesce(1).write.format("com.databricks.spark.csv").save("twitter")
  }


  def getFromKeywordSingleDay(i: DateTime,k: String, count: Int): String = {
    val consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret)
    consumer.setTokenWithSecret(AccessToken, AccessSecret)
    val url = "https://api.twitter.com/1.1/search/tweets.json?q=" + k + "&count=" + count + "&until=" + i.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd"))
    //println(url)
    val request = new HttpGet(url)
    consumer.sign(request)
    val client = HttpClientBuilder.create().build()
    val response = client.execute(request)
    IOUtils.toString(response.getEntity().getContent())
  }


  def getFromKeyword(k: String, count: Int = 90): String = {
    val today= DateTime.now
    //println(today.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd")))
    val ss = for (i <- 1 to 7) yield getFromKeywordSingleDay(today-i.days,k,count)
    ss.mkString("\n")
  }
}