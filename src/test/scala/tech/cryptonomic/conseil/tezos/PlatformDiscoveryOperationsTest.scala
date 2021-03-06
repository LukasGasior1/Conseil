package tech.cryptonomic.conseil.tezos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience}
import org.scalatest.{Matchers, OptionValues, WordSpec}
import tech.cryptonomic.conseil.tezos.FeeOperations.AverageFees
import tech.cryptonomic.conseil.tezos.PlatformDiscoveryTypes.{Attributes, DataType, KeyType, Network}

import scala.concurrent.duration._


class PlatformDiscoveryOperationsTest
  extends WordSpec
    with InMemoryDatabase
    with MockFactory
    with Matchers
    with ScalaFutures
    with OptionValues
    with IntegrationPatience
    with LazyLogging {

  import slick.jdbc.PostgresProfile.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  "getNetworks" should {
    "return list with one element" in {
      val cfg = ConfigFactory.parseString(
        """
          | platforms.tezos : {
          |  alphanet: {
          |    node: {
          |      protocol: "http",
          |      hostname: "localhost",
          |      port: 8732
          |      pathPrefix: ""
          |    }
          |  }
          | }
        """.stripMargin)

      PlatformDiscoveryOperations.getNetworks(cfg) shouldBe List(Network("alphanet", "Alphanet", "tezos", "alphanet"))
    }
    "return two networks" in {
      val cfg = ConfigFactory.parseString(
        """
          |platforms.tezos : {
          |  alphanet: {
          |    node: {
          |      protocol: "http",
          |      hostname: "localhost",
          |      port: 8732
          |      pathPrefix: ""
          |    }
          |  }
          |  alphanet-staging : {
          |    node: {
          |      protocol: "https"
          |      hostname: "nautilus.cryptonomic.tech",
          |      port: 8732
          |      pathPrefix: "tezos/alphanet/"
          |    }
          |  }
          |}
        """.stripMargin)

      PlatformDiscoveryOperations.getNetworks(cfg).size shouldBe 2
    }
  }

  "getEntities" should {

    "return list of attributes of Fees" in {

      dbHandler.run {
        PlatformDiscoveryOperations.makeAttributesList("fees")
      }.futureValue shouldBe
        List(
          Attributes("low", "Low", DataType.Int, 0, KeyType.UniqueKey, "fees"),
          Attributes("medium", "Medium", DataType.Int, 0, KeyType.UniqueKey, "fees"),
          Attributes("high", "High", DataType.Int, 0, KeyType.UniqueKey, "fees"),
          Attributes("timestamp", "Timestamp", DataType.DateTime, 0, KeyType.UniqueKey, "fees"),
          Attributes("kind", "Kind", DataType.String, 0, KeyType.UniqueKey, "fees")
        )
    }

    "return list of attributes of accounts" in {
      dbHandler.run {
        PlatformDiscoveryOperations.makeAttributesList("accounts")
      }.futureValue shouldBe
        List(
          Attributes("account_id", "Account id", DataType.String, 0, KeyType.UniqueKey, "accounts"),
          Attributes("block_id", "Block id", DataType.String, 0, KeyType.UniqueKey, "accounts"),
          Attributes("manager", "Manager", DataType.String, 0, KeyType.UniqueKey, "accounts"),
          Attributes("spendable", "Spendable", DataType.Boolean, 0, KeyType.UniqueKey, "accounts"),
          Attributes("delegate_setable", "Delegate setable", DataType.Boolean, 0, KeyType.UniqueKey, "accounts"),
          Attributes("delegate_value", "Delegate value", DataType.String, 0, KeyType.UniqueKey, "accounts"),
          Attributes("counter", "Counter", DataType.Int, 0, KeyType.UniqueKey, "accounts"),
          Attributes("script", "Script", DataType.String, 0, KeyType.UniqueKey, "accounts"),
          Attributes("balance", "Balance", DataType.Decimal, 0, KeyType.UniqueKey, "accounts"),
          Attributes("block_level", "Block level", DataType.Decimal, 0, KeyType.UniqueKey, "accounts")
        )
    }

    "return list of attributes of blocks" in {
      dbHandler.run {
        PlatformDiscoveryOperations.makeAttributesList("blocks")
      }.futureValue shouldBe
        List(
          Attributes("level", "Level", DataType.Int, 0, KeyType.UniqueKey, "blocks"),
          Attributes("proto", "Proto", DataType.Int, 0, KeyType.UniqueKey, "blocks"),
          Attributes("predecessor", "Predecessor", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("timestamp", "Timestamp", DataType.DateTime, 0, KeyType.UniqueKey, "blocks"),
          Attributes("validation_pass", "Validation pass", DataType.Int, 0, KeyType.UniqueKey, "blocks"),
          Attributes("fitness", "Fitness", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("context", "Context", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("signature", "Signature", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("protocol", "Protocol", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("chain_id", "Chain id", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("hash", "Hash", DataType.String, 0, KeyType.UniqueKey, "blocks"),
          Attributes("operations_hash", "Operations hash", DataType.String, 0, KeyType.UniqueKey, "blocks")
        )
    }

    "return list of attributes of operations" in {
      dbHandler.run {
        PlatformDiscoveryOperations.makeAttributesList("operations")
      }.futureValue shouldBe
        List(
          Attributes("kind", "Kind", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("source", "Source", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("amount", "Amount", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("destination", "Destination", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("balance", "Balance", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("delegate", "Delegate", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("operation_group_hash", "Operation group hash", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("operation_id", "Operation id", DataType.Int, 0, KeyType.UniqueKey, "operations"),
          Attributes("fee", "Fee", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("storage_limit", "Storage limit", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("gas_limit", "Gas limit", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("block_hash", "Block hash", DataType.String, 0, KeyType.UniqueKey, "operations"),
          Attributes("timestamp", "Timestamp", DataType.DateTime, 0, KeyType.UniqueKey, "operations"),
          Attributes("block_level", "Block level", DataType.Int, 0, KeyType.UniqueKey, "operations"),
          Attributes("pkh", "Pkh", DataType.String, 0, KeyType.UniqueKey, "operations")
        )
    }

    "return list of attributes of operation groups" in {
      dbHandler.run {
        PlatformDiscoveryOperations.makeAttributesList("operation_groups")
      }.futureValue shouldBe
        List(
          Attributes("protocol", "Protocol", DataType.String, 0, KeyType.UniqueKey, "operation_groups"),
          Attributes("chain_id", "Chain id", DataType.String, 0, KeyType.UniqueKey, "operation_groups"),
          Attributes("hash", "Hash", DataType.String, 0, KeyType.UniqueKey, "operation_groups"),
          Attributes("branch", "Branch", DataType.String, 0, KeyType.UniqueKey, "operation_groups"),
          Attributes("signature", "Signature", DataType.String, 0, KeyType.UniqueKey, "operation_groups"),
          Attributes("block_id", "Block id", DataType.String, 0, KeyType.UniqueKey, "operation_groups")
        )
    }

    "return empty list for non existing table" in {
      dbHandler.run {
        PlatformDiscoveryOperations.makeAttributesList("nonExisting")
      }.futureValue shouldBe List.empty
    }
  }

  "listAttributeValues" should {

    "return list of values of kind attribute of Fees without filter" in {
      val avgFee = AverageFees(1, 3, 5, Timestamp.valueOf(LocalDateTime.of(2018, 11, 22, 12, 30)), "example1")
      dbHandler.run(TezosDatabaseOperations.writeFees(List(avgFee))).isReadyWithin(5.seconds)

      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", None)
      ).futureValue shouldBe List("example1")
    }

    "returns a failed future when asked for medium attribute of Fees without filter - numeric attributes should not be displayed" in {
      val avgFee = AverageFees(1, 3, 5, Timestamp.valueOf(LocalDateTime.of(2018, 11, 22, 12, 30)), "example1")

      dbHandler.run(TezosDatabaseOperations.writeFees(List(avgFee))).isReadyWithin(5.seconds)

      intercept[NoSuchElementException] {
        throw dbHandler.run(
          PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "medium", None)
        ).failed.futureValue
      }
    }

    "return empty list when trying to sql inject" in {
      val avgFee = AverageFees(1, 3, 5, Timestamp.valueOf(LocalDateTime.of(2018, 11, 22, 12, 30)), "example1")

      dbHandler.run(TezosDatabaseOperations.writeFees(List(avgFee))).isReadyWithin(5.seconds)
      // That's how the SLQ-injected string will look like:
      // SELECT DISTINCT kind FROM fees WHERE kind LIKE '%'; DELETE FROM fees WHERE kind LIKE '%'
      val maliciousFilter = Some("'; DELETE FROM fees WHERE kind LIKE '")

      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", maliciousFilter)
      ).futureValue shouldBe List.empty

      dbHandler.run(Tables.Fees.length.result).futureValue shouldBe 1

    }
    "correctly apply the filter" in {
      val avgFees = List(
        AverageFees(1, 3, 5, Timestamp.valueOf(LocalDateTime.of(2018, 11, 22, 12, 30)), "example1"),
        AverageFees(2, 4, 6, Timestamp.valueOf(LocalDateTime.of(2018, 11, 22, 12, 31)), "example2")
      )

      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", Some("1"))
      ).futureValue shouldBe List.empty
      dbHandler.run(TezosDatabaseOperations.writeFees(avgFees)).isReadyWithin(5.seconds)
      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", None)
      ).futureValue should contain theSameElementsAs List("example1", "example2")
      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", Some("ex"))
      ).futureValue should contain theSameElementsAs List("example1", "example2")
      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", Some("ample"))
      ).futureValue should contain theSameElementsAs List("example1", "example2")
      dbHandler.run(
        PlatformDiscoveryOperations.verifyAttributesAndGetQueries("fees", "kind", Some("1"))
      ).futureValue shouldBe List("example1")

    }

  }

}
