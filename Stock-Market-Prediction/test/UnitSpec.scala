import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import services.Counter
import Util.Timeseries._

/**
 * Runs a browser test using Fluentium against a play application on a server port.
 */
class UnitSpec extends PlaySpec
  with OneBrowserPerTest
  with GuiceOneServerPerTest
  with HtmlUnitFactory
  with ServerProvider {

  "SparkContext" should {

    "match timeseries AppName" in {

      spark.sparkContext.appName must equal("Stock-prediction")

    }
  }
}
