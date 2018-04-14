package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by vinay on 4/17/17.
  */

@Singleton
class UserController @Inject()() extends Controller {


//    def graph1 = Action.async {
//        Future(Util.Timeseries.abc()) map (x => Ok(Json.toJson(x)))
//    }


}
