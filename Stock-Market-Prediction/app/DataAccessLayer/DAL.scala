package DataAccessLayer

import javax.inject.{Inject, Singleton}

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile


abstract class DAL(databaseConfig: DatabaseConfigProvider) {
  lazy val dbConfig = databaseConfig.get[JdbcProfile]
}
