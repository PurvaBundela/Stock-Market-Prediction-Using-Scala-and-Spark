package Util

import Util.SentimentAnalysis
import twitter4j._
import scala.util._
import services.Response
import Util.Ingest
import java.util.Properties
import scala.io.{Codec, Source}

object CalcAvgScore {

  System.setProperty("twitter4j.oauth.consumerKey", TwitterMain.ConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", TwitterMain.ConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", TwitterMain.AccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterMain.AccessSecret)

  def calcSentiment(k: String = "", count: Int = 90, catchlog: Boolean = true): Double = {
    val ingester = new Ingest[Response]()
    implicit val codec = Codec.UTF8
    val source = Source.fromString(TwitterMain.getFromKeyword(k.replaceAll(" ","%20"),count))
    val realtimetweet = for (tweet <- ingester(source).toSeq) yield tweet
    val processed = realtimetweet.flatMap(_.toOption)
    val totalsentiment = processed.map(r => r.statuses)
    val finaltweet = totalsentiment.flatten
    val sumofsentiment = finaltweet.par.map(s => SentimentAnalysis.detectSentimentScore(s.text,catchlog))
    val score = sumofsentiment.sum/sumofsentiment.size
    score
  }

}
