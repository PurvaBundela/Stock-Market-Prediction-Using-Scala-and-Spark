package Util

import akka.actor.ActorSystem
import org.scalatestplus.play._
import Util.Timeseries._
import org.apache.spark.mllib.linalg.{Vector, Vectors}

import scala.collection.mutable.ListBuffer

class TimeseriesTest extends PlaySpec {

//    "Comapnies Length" should {
//
//        "match timeseries companyLength" in {
//            val companies = trainAndPredictPrice()
//            companies.length must equal(9)
//
//        }
//    }

    "Spark Conf" should {

        "match timeseries AppName" in {
            conf.get("spark.app.name") must equal("Stock-prediction")


        }

        "match timeseries version" in {
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

    "TopCompany" should {

        "match timeseries topCompany" in {
            val dv = Vectors.dense(40.0,20.0,10.0,30.0,15.0,60.0)

            val name = "Amazon"
            val dv1 = Vectors.dense(40.0,20.0,10.0,30.0,15.0,35.0)
            val name1 = "Apple"
            val array : Array[(String, Vector)] = Array()
            array:+((name,dv)):+((name1,dv1))
            val result : Array[(Double, String)] = Array()
            result:+((20.0,"Amazon")):+((-5.0,"Apple"))
            val repeatName = getTopThreeProfitableCompanies(array)
            repeatName must equal(result)
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
