package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

/**
 * DaoRunner class for running Slick database actions.
 */
@ImplementedBy(classOf[DaoRunnerImpl])
trait DaoRunner {
  import slick.dbio._

  /**
   * Runs the given action in a transaction
   */
  def run[R](a: DBIO[R]): Future[R]
}

@Singleton
class DaoRunnerImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: DbExecutionContext)
  extends DaoRunner with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def run[R](a: DBIO[R]): Future[R] = db.run(a.transactionally)

}
