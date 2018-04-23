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




object TwitterClient {

  val ConsumerKey = "zckox0ybm1UheRy7DdirLYB18"
  val ConsumerSecret = "Nx0oDdxfZrRFepfbuMLFqMRvjAhYduaHPwlRCW5r0oHFypwmVn"
  val AccessToken = "988311065896374272-ECPkJhP3GYbJ38RTUDMDNQJuhmeiAWg"
  val AccessSecret = "LuL3oMyVNDpMpVTEmjokScshuoR5ZoozHB42fkcJsS81m"

  def getFromSearchApiByKeyword(k: String, count: Int = 90): String = {
    val today= DateTime.now
    //println(today.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd")))
    val ss = for (i <- 1 to 7) yield getFromSearchApiByKeywordForOneDay(today-i.days,k,count)
    ss.mkString("\n")
  }

  def getFromSearchApiByKeywordForOneDay(i: DateTime,k: String, count: Int): String = {
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

  def main(args: Array[String]) {

    if (args.size == 0) println("Please refer to README.md in our repo for input parameter.") else {
      val keyword = if (args.size > 1) args.tail.mkString(" ") else ""

      args(0) match {
        case "stock" => runStock(keyword)
        case _ => println("Invalid input. Please refer to README.md in our repo for input parameter.")
      }
    }
  }

  def runStock(k: String) = {
    k match {
      case "" =>
        val aapleScore = Usecases.calcSentimentFromSearchApi("Apple Inc stock")
        val amazonScore = Usecases.calcSentimentFromSearchApi("Amazon.com Inc stock")
        val  ebayScore = Usecases.calcSentimentFromSearchApi("eBay Inc stock")
        val  walmartScore = Usecases.calcSentimentFromSearchApi("Walmart Inc stock")
        val  tripAdvisorScore = Usecases.calcSentimentFromSearchApi("Tripadvisor Inc stock")
        val  googleScore = Usecases.calcSentimentFromSearchApi("Alphabet Inc stock")
        val  facebookScore = Usecases.calcSentimentFromSearchApi("Facebook Inc stock")
        val  microsoftScore = Usecases.calcSentimentFromSearchApi("Microsoft Corporation stock")
        val  expediaScore = Usecases.calcSentimentFromSearchApi(" Expedia Group Inc stock")

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
//        println("Apple Inc vs GNC:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("GNC stock")))
//        println("Apple Inc vs Netflix:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Netflix stock")))
//        println("Apple Inc vs Bank of America:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Bank of America stock")))
//        println("Apple Inc vs Comcast:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Comcast stock")))
//        println("Apple Inc vs Wells Fargo:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Wells Fargo stock")))
//        println("Apple Inc vs Verizon:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Verizon stock")))
//        println("Apple Inc vs Citi:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Citi stock")))
//        println("Apple Inc vs ATT:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("ATT stock")))
//        println("Apple Inc vs United Continental:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("United Continental stock")))
//        println("Apple Inc vs Delta Air Lines:" + Usecases.compareSentiment(aaplScore, Usecases.calcSentimentFromSearchApi("Delta Air Lines stock")))
      case _ =>
        println(k+" stock sentiment score:"+Usecases.calcSentimentFromSearchApi(k + " stock"))
    }
  }
}