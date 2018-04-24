package Util

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.util.logging.RedwoodConfiguration
import scala.collection.JavaConversions._

/**
  *
  * reference:https://github.com/vspiewak/twitter-sentiment-analysis/blob/master/src/main/scala/com/github/vspiewak/util/SentimentAnalysisUtils.scala
  */
object SentimentAnalysis {

  val nlpProps = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment")
    props
  }

  def detectSentiment(message: String, catchlog: Boolean = true): SENTIMENT_TYPE = {

    detectSentimentScore(message,catchlog) match {
      case s if s <= 0.0 => NOT_UNDERSTOOD
      case s if s < 1.0 => VERY_NEGATIVE
      case s if s < 2.0 => NEGATIVE
      case s if s < 3.0 => NEUTRAL
      case s if s < 4.0 => POSITIVE
      case s if s < 5.0 => VERY_POSITIVE
      case s if s > 5.0 => NOT_UNDERSTOOD
    }

  }

 

  trait SENTIMENT_TYPE
  case object VERY_NEGATIVE extends SENTIMENT_TYPE
  case object NEGATIVE extends SENTIMENT_TYPE
  case object NEUTRAL extends SENTIMENT_TYPE
  case object POSITIVE extends SENTIMENT_TYPE
  case object VERY_POSITIVE extends SENTIMENT_TYPE
  case object NOT_UNDERSTOOD extends SENTIMENT_TYPE

  def replaceSpecialChar(s: String): String = s.replaceAll("[^\\p{L}\\p{N}\\p{Z}\\p{Sm}\\p{Sc}\\p{Sk}\\p{Pi}\\p{Pf}\\p{Pc}\\p{Mc}]","")

  def detectSentimentScore(message: String, catchlog: Boolean = true): Double = {
    if (catchlog == true) RedwoodConfiguration.empty().capture(System.err).apply()

    val pipeline = new StanfordCoreNLP(nlpProps)
    val annotation = pipeline.process(replaceSpecialChar(message))
    val slp = for (sentence <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation])) yield {
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      val partText = sentence.toString
      (sentiment.toDouble,partText.length)
    }

    val weightedSentiments = slp.unzip.zipped.map((sentiment, size) => sentiment * size)
    val sizes = slp.unzip._2
    val weightedSentiment = weightedSentiments.sum / sizes.sum

    if (catchlog == true) RedwoodConfiguration.current().clear().apply()
    /*
     0 -> very negative
     1 -> negative
     2 -> neutral
     3 -> positive
     4 -> very positive
     */
    weightedSentiment

  }
}
