package actors

import DataAccessLayer.UserDalImpl
import actors.LoginActor.{CreateUser, GetUser}
import akka.actor.Actor
import akka.pattern.pipe
import models.User

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by akashnagesh on 4/9/17.
  */

object LoginActor {

  case class GetUser(email: String, password: String)

  case class CreateUser(user: User)

}

class LoginActor(userDalImpl: UserDalImpl) extends Actor {

  override def receive: Receive = {
    case GetUser(email: String, password: String) => {
      pipe(userDalImpl.getUser(email, password))
    } to sender()

    case CreateUser(user: User) => {
      userDalImpl.addUser(user) pipeTo (sender())
    }
  }
}

