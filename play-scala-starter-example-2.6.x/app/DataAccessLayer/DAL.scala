package DataAccessLayer

import javax.inject.{Inject, Singleton}

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by akashnagesh on 4/7/17.
  */


abstract class DAL(databaseConfig: DatabaseConfigProvider) {
  lazy val dbConfig = databaseConfig.get[JdbcProfile]
}
