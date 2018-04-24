package DataAccessLayer

import javax.inject.Singleton

import com.google.inject.Inject
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserActionMessages {
  val emailAlreadyExists = "DUPLICATE_EMAIL"
  val userInsertionSuccessfull = "USER_INSERTED"
  val genericError = "GENERIC_ERROR"
}

trait UserDal {
  def addUser(user: User): Future[String]

  def getUser(email: String, password: String): Future[Option[User]]

}

@Singleton
class UserDalImpl @Inject()(databaseConfig: DatabaseConfigProvider) extends DAL(databaseConfig) with UserDal {

  import dbConfig._

  private class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def email = column[String]("email")

    def age = column[Int]("age")

    def password = column[String]("password")

    override def * = (id, name, age, email, password) <> ((User.apply _).tupled, User.unapply _)

  }

  private val users = TableQuery[UserTableDef]


  override def addUser(user: User): Future[String] = db.run(users += user).map(_ => UserActionMessages.userInsertionSuccessfull)
    .recover {
      case duplicateEmail: java.sql.SQLIntegrityConstraintViolationException => UserActionMessages.emailAlreadyExists
      case ex: Exception => UserActionMessages.genericError
    }

  override def getUser(email: String, password: String): Future[Option[User]] = {
    db.run {
      users.filter(_.email === email).filter(_.password === password).result.headOption
    }
  }
}
