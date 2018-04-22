package actors

import DataAccessLayer.UserDalImpl
import actors.LoginActor.{CreateUser, GetUser}
import actors.StockActor.{Stock, getStocks}
import akka.actor.{Actor, Props}
import models.User
import scala.util.{Failure, Success}
import akka.pattern.pipe
import Util.Timeseries


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object StockActor {
  def props = Props[StockActor]

  case class Stock(name: String)

  case class getStocks(test: String)

}

class StockActor extends Actor {
    val test1 = Timeseries.trainAndPredictPrice()
    //Thread.sleep(20000)
    //test1.foreach(x=>println(x))
  def receive: Receive = {
    case Stock(name:String) =>
      sender() ! name

    case getStocks(test:String) => {
      print("in getsticks")
      val a = test1
      //a.foreach(x=>x.toString().concat())
      //Thread.sleep(20000)

      sender() ! a


    }
  }
}

