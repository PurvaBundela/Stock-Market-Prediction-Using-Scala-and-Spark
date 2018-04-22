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


  val filterLanguage = {
    status:Status => status.getLang == "en"
  }

  val getTextAndSentiment = {
    status:Status => (status.getText(), SentimentAnalysis.detectSentimentScore(status.getText()))
  }


  val countsAveSentimentScore = {
    a:(String,(Int,Double)) => (a._1, (a._2._1, a._2._2 / a._2._1))
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
