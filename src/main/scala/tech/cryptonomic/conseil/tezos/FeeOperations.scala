package tech.cryptonomic.conseil.tezos

import com.codahale.metrics.{MetricRegistry, Timer}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tech.cryptonomic.conseil.Lorre.db
<<<<<<< HEAD
import tech.cryptonomic.conseil.tezos.{TezosDatabaseOperations => Tdb}

import scala.concurrent.{Future, ExecutionContext}
=======

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}
>>>>>>> benchmarks/blocking-fees-computation
import scala.util.{Failure, Success, Try}
import slick.dbio.DBIOAction

/**
  * Helper classes and functions used for average fee calculations.
  */
object FeeOperations extends LazyLogging {

  private val operationKinds = List(
    "seed_nonce_revelation",
    "delegation",
    "transaction",
    "activate_account",
    "origination",
    "reveal",
    "double_endorsement_evidence",
    "endorsement"
  )

  /**
    * Representation of estimation of average fees for a given operation kind.
    * @param low       Medium - one standard deviation
    * @param medium    The mean of fees on a given operation kind
    * @param high      Medium + one standard deviation
    * @param timestamp The timestamp when the calculation took place
    * @param kind      The kind of operation being averaged over
    */
  case class AverageFees(
                 low: Int,
                 medium: Int,
                 high: Int,
                 timestamp: java.sql.Timestamp,
                 kind: String
                 )

  /**
    * Calculates average fees for each operation kind and stores them into a fees table.
    * @return
    */
  def processTezosAverageFees(timer: Option[Timer])(implicit ex: ExecutionContext): Future[Option[Int]] = {
    logger.info("Processing latest Tezos fee data...")
    val timing = timer.map(_.time())
    val computeAndStore = for {
      fees <- DBIOAction.sequence(operationKinds.map(Tdb.calculateAverageFeesIO))
      dbWrites <- Tdb.writeFeesIO(fees.collect{ case Some(fee) => fee })
    } yield dbWrites

    db.run(computeAndStore)
      .andThen {
        case _ => timing.foreach(_.stop())
      }
      .andThen{
        case Success(Some(written)) => logger.info("Wrote {} average fees to the database.", written)
        case Success(None) => logger.info("Wrote average fees to the database.")
        case Failure(e) => logger.error("Could not write average fees to the database because", e)
      }

  }

}
