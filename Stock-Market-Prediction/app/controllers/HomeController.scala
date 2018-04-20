package controllers

import javax.inject.Inject
import actors.{LoginActor, StockActor}
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.stream.{Materializer, Supervision}
import akka.util.Timeout
import DataAccessLayer.{UserActionMessages, UserDalImpl}
import actors.StockActor.{Stock, getStocks}
import models.{FormsData, User}
import org.apache.commons.lang3.StringUtils
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

class HomeController @Inject()(val controllerComponents: ControllerComponents)(userDalImpl: UserDalImpl)
                              (implicit ec: ExecutionContext, system: ActorSystem, materializer: Materializer) extends BaseController with I18nSupport{

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  //val logger = play.Logger.of("airbnb_logger")
  //val logger = play.Logger.of("airbnb_logger")

  val loginRouter = system.actorOf(Props(classOf[LoginActor], userDalImpl).withRouter(RoundRobinPool(10)), name = "LoginActor")
  def index = Action { implicit request =>
    //logger.info("User in landing page")
    Ok(views.html.index(FormsData.userForm)(FormsData.createUserForm)(StringUtils.EMPTY))
  }

//    val stockRouter = system.actorOf(StockActor.props,"Test")
//    implicit val timeout: Timeout = 30.seconds
//  def userLogin = Action.async {
//      implicit request =>
//          Supervision.Restart
//         println("userlogin")
//      (stockRouter ? getStocks("abc")).mapTo[Double].map{
//          abc => Ok(views.html.loggedInPage(abc))
//      }
////
////
//////
//////
////      FormsData.userForm.bindFromRequest().fold(
////        formWithErrors => Future.successful(BadRequest),
////          abc => {
////              stockRouter ? StockActor.Stock("abc")
////          }.mapTo[String].map {
////              Ok(views.html.loggedInPage(abc))
////////          case Some(user) =>
////////          case None => Ok("Invalid credentials")
//////        }
////      )
//  }
    val abc = Util.Timeseries.abc()
    def userLogin = Action.async {
        implicit request =>
            implicit val timeout: Timeout = Timeout(2 seconds)
            FormsData.userForm.bindFromRequest().fold(
                formWithErrors => Future.successful(BadRequest),
                userTuple => {
                    loginRouter ? LoginActor.GetUser(userTuple._1, userTuple._2)
                } map {
                    case Some(user) => Ok(views.html.loggedInPage(abc)).withSession("user" -> userTuple._1)
                    case None => Ok("Invalid credentials")
                }
            )
    }

       def demo = Action {
    Ok(views.html.demo())
  }                         

  def createUser = Action.async {
    implicit request =>
      implicit val timeout: Timeout = Timeout(2 seconds)
      FormsData.createUserForm.bindFromRequest().fold(
        errorForm => Future.successful(Ok),
        user => {
          val user1 = User(0, user.name, user.age, user.email, user.password)
          loginRouter ? LoginActor.CreateUser(user1)
        } map (someMes => someMes match {
          case UserActionMessages.emailAlreadyExists => Ok(views.html.index(FormsData.userForm)(FormsData.createUserForm)("Email Id Already Exists"))
          case UserActionMessages.genericError => Ok(views.html.index(FormsData.userForm)(FormsData.createUserForm)("Unable to create user. Please try again."))
          case _ => Ok(views.html.index(FormsData.userForm)(FormsData.createUserForm)("User account created! Login to use our service"))
        })
      )

  }

}
