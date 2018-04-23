package Util


import Util.SentimentUtils
import twitter4j._
import scala.util._
import Util.{Ingest, Response}
import java.util.Properties
import scala.io.{Codec, Source}

object Usecases {


  System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)






  val filterLanguage = {
    status:Status => status.getLang == "en"
  }

  val getTextAndSentiment = {
    status:Status => (status.getText(), SentimentUtils.detectSentimentScore(status.getText()))
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
    }, SentimentUtils.detectSentimentScore(status.getText()))
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





  def calcSentimentFromSearchApi(k: String = "", count: Int = 90, catchlog: Boolean = true): Double = {
    val ingester = new Ingest[Response]()
    implicit val codec = Codec.UTF8
    val source = Source.fromString(TwitterClient.getFromSearchApiByKeyword(k.replaceAll(" ","%20"),count))
    val rts = for (t <- ingester(source).toSeq) yield t
    val rs = rts.flatMap(_.toOption)
    val tss = rs.map(r => r.statuses)
    //for (t <- tss) println(t.size)

    val ts = tss.flatten

    //println(ts.size)

    val sts = ts.par.map(s => SentimentUtils.detectSentimentScore(s.text,catchlog))

    val avgscore = sts.sum/sts.size
    //println(avgscore)

    avgscore
  }

  def compareSentiment(a:Double,b:Double) :Boolean = a > b
}
