package Util

import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import services.Counter
import Util.Timeseries._

import scala.collection.mutable.ListBuffer

class TimeseriesTest extends PlaySpec {

    "Comapnies Length" should {

        "match timeseries companyLength" in {
            val companies = trainAndPredictPrice()
            companies.length must equal(9)

        }
    }

    "Spark Conf" should {

        "match timeseries AppName" in {
            conf.get("spark.app.name") must equal("Stock-prediction")


        }
    }

    "RepeatCompany" should {

        "match timeseries companyList" in {
            val list = List("Amazon","Apple","Google")
            val repeatName = createMultipleCompanyValues(3,list)
            repeatName must equal(List("Amazon","Amazon","Amazon","Apple","Apple","Apple","Google","Google","Google"))
        }
    }

//    test("testCreateMultipleCompanyValues") {
//
//    }
//
//    test("testPriceForecast") {
//
//    }
//
//    test("testPriceForecast_$eq") {
//
//    }
//
//    test("testTrainAndPredictPrice") {
//
//    }
//
//    test("testGetTopThreeProfitableCompanies") {
//
//    }
//
//    test("testSaveCompanyPredictionValues") {
//
//    }

}
