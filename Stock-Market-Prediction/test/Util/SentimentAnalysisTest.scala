package Util
import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}
import scala.util._
import Util.SentimentAnalysis.replaceSpecialChar
import org.scalatest.FunSuite
import  Util.Ingest
import  services.Tweet

class SentimentAnalysisTest extends  FlatSpec with Matchers {

  behavior of "detectSentimentScore"

  it should "detect 3.0" in {
    SentimentAnalysis.detectSentimentScore("It was nice meeting you.",false) shouldBe 3.0
  }


  //behavior of "replaceSpecialChar"

  it should "work" in {
    replaceSpecialChar("&*%Scala") shouldBe "Scala"
  }

  it should "detect 1.0" in {
    SentimentAnalysis.detectSentimentScore("I hate it.",false) shouldBe 1.0
  }

  it should "detect 2.0" in {
    SentimentAnalysis.detectSentimentScore("I it.",false) shouldBe 2.0
  }




}
