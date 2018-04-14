package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by vinay on 4/5/17.
  */
case class UserFormData(name: String, age: Int, email: String, password: String)

case class User(id: Long, name: String, age: Int, email: String, password: String)


