package Util


import Util.SentimentAnalysis
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j._
import Util.{Ingest, Response}

import scala.io.{Codec, Source}

object Usecases {


  System.setProperty("twitter4j.oauth.consumerKey", TwitterRequest.ConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", TwitterRequest.ConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", TwitterRequest.AccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterRequest.AccessSecret)


  def popularHashTags(k: String = ""): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    // create a SparkContext
    val sc = new SparkContext("local[*]", "PopularHashtags")

    val ssc = new StreamingContext(sc, Seconds(1))

    // create a DStream from Twitter using our streaming context
    val tweets = k match {
      case "" => TwitterUtils.createStream(ssc, None)
      case _  => TwitterUtils.createStream(ssc, None, Seq(k))
    }

    val sortedResults = tweets.filter(filterLanguage)
                              .map(getTextAndSentiment)
                              .map(getHashTags)
                              .map(addCountToHashTags)
                              .reduceByKeyAndWindow(plusForTwo, minusForTwo, Seconds(600), Seconds(1))
                              .map(countsAveSentimentScore)
                              .transform(rdd => rdd.sortBy(x => x._2._1, false))

    // print the top 10
    sortedResults.print

    // set a checkpoint directory, and start
    ssc.checkpoint("testdata/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }

  val filterLanguage = {
    status:Status => status.getLang == "en"
  }

  val getTextAndSentiment = {
    status:Status => (status.getText(), SentimentAnalysis.detectSentimentScore(status.getText()))
  }

  val getHashTags = {
    a:(String,Double) => (a._1.split(" ").filter(b => b.startsWith("#")).headOption.getOrElse("NoHashTag"), a._2)
  }

  val addCountToHashTags = {
    a:(String,Double) => (a._1, (1, a._2))
  }

  val plusForTwo = {
    (x:(Int,Double), y:(Int,Double)) => (x._1 + y._1, x._2 + y._2)
  }

  val minusForTwo = {
    (x:(Int,Double), y:(Int,Double)) => (x._1 - y._1, x._2 - y._2)
  }

  val countsAveSentimentScore = {
    a:(String,(Int,Double)) => (a._1, (a._2._1, a._2._2 / a._2._1))
  }

  val getLocationAndSentiment = {
    status:Status => (Option(status.getGeoLocation) match {
      case Some(g) => matchLocation(g)
      case _ => "null"
    }, SentimentAnalysis.detectSentimentScore(status.getText()))
  }

  val filterContainGeoLocation = {
    status:Status => Option(status.getGeoLocation) match {
      case Some(_) => true
      case None => false
    }
  }


  def matchLocation(g:GeoLocation):String = g match {
    case g if nearLocation(g,40.730610, -73.935242)  => "New York City, NY, USA,40.730610, -73.935242"
    case g if nearLocation(g,34.052235, -118.243683)  => "Los Angeles, CA, USA,34.052235, -118.243683"
    case g if nearLocation(g,47.608013, -122.335167)  => "Seattle, WA, USA,47.608013, -122.335167"
    case g if nearLocation(g,29.761993, -95.366302)  => "Houston, TX, USA,29.761993, -95.366302"
    case g if nearLocation(g,25.761681, -80.191788)  =>  "Miami, FL, USA,25.761681, -80.191788"
    case g if nearLocation(g,51.515419, -0.141099)  => "London, UK,51.515419, -0.141099"
    case g if nearLocation(g,43.653908, -79.384293)  => "Toronto, ON, Canada,43.653908, -79.384293"
    case g if nearLocation(g,-33.865143, 151.209900)  => "Sydney, NSW, Australia,-33.865143, 151.209900"
    case g if nearLocation(g,19.073212, 72.854195)  => "Mumbai, India,19.073212, 72.854195"
    case _ => "Other Location"
  }

  def nearLocation(g:GeoLocation,latitude: Double,longitude: Double) = {
    //println(g)
    (math.abs(g.getLatitude - latitude) < 2) && (math.abs(g.getLongitude - longitude) < 2)
  }



  def popularLocations(k: String = ""): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    // create a SparkContext
    val sc = new SparkContext("local[*]", "PopularLocations")

    val ssc = new StreamingContext(sc, Seconds(1))

    // create a DStream from Twitter using our streaming context
    val tweets = k match {
      case "" => TwitterUtils.createStream(ssc, None)
      case _  => TwitterUtils.createStream(ssc, None, Seq(k))
    }

    val sortedResults = tweets.filter(filterLanguage)
                              .filter(filterContainGeoLocation)
                              .map(getLocationAndSentiment)
                              .map(addCountToHashTags)
                              .reduceByKeyAndWindow(plusForTwo, minusForTwo, Seconds(36000), Seconds(1))
                              .map(countsAveSentimentScore)
                              .transform(rdd => rdd.sortBy(x => x._2._1, false))

    // print the top 10
    sortedResults.print


    // set a checkpoint directory, and start
    ssc.checkpoint("testdata/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }


  def calcSentimentFromSearchApi(k: String = "", count: Int = 90, catchlog: Boolean = true): Double = {
    val ingester = new Ingest[Response]()
    implicit val codec = Codec.UTF8
    val source = Source.fromString(TwitterRequest.getFromSearchApiByKeyword(k.replaceAll(" ","%20"),count))
    val rts = for (t <- ingester(source).toSeq) yield t
    val rs = rts.flatMap(_.toOption)
    val tss = rs.map(r => r.statuses)
    for (t <- tss) println(t.size)

    val ts = tss.flatten



    val sts = ts.par.map(s => SentimentAnalysis.detectSentimentScore(s.text,catchlog))

    val avgscore = sts.sum/sts.size


    avgscore
  }

  def compareSentiment(a:Double,b:Double) :Boolean = a > b
}
