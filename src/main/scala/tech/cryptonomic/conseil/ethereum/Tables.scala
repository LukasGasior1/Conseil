package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString
import slick.jdbc.PostgresProfile.api._

object Tables {

  implicit val byteStringMapper = MappedColumnType.base[ByteString, String](ByteUtils.toHexString, ByteUtils.decodeHex)
  implicit val blockHashMapper = MappedColumnType.base[BlockHash, String](_.toString, s => BlockHash(ByteUtils.decodeHex(s)))
  implicit val transactionHashMapper = MappedColumnType.base[TransactionHash, String](_.toString, s => TransactionHash(ByteUtils.decodeHex(s)))
  implicit val addressMapper = MappedColumnType.base[Address, String](_.toString, s => Address(ByteUtils.decodeHex(s)))
  implicit val bigIntMapper = MappedColumnType.base[BigInt, Long](_.toLong, BigInt.apply)

  class Blocks(tag: Tag) extends Table[Block](tag, "BLOCK") {
    def number = column[BigInt]("NUMBER")
    def hash = column[BlockHash]("HASH", O.PrimaryKey)
    def parentHash = column[BlockHash]("PARENT_HASH")
    def sha3Uncles = column[ByteString]("SHA3_UNCLES")
    def logsBloom = column[ByteString]("LOGS_BLOOM")
    def transactionsRoot = column[ByteString]("TRANSACTIONS_ROOT")
    def stateRoot = column[ByteString]("STATE_ROOT")
    def difficulty = column[BigInt]("DIFFICULTY")
    def totalDifficulty = column[BigInt]("TOTAL_DIFFICULTY")
    def extraData = column[ByteString]("EXTRA_DATA")
    def size = column[BigInt]("SIZE")
    def gasLimit = column[BigInt]("GAS_LIMIT")
    def gasUsed = column[BigInt]("GAS_USED")
    def timestamp = column[BigInt]("TIMESTAMP")

    def * = (number, hash, parentHash, sha3Uncles, logsBloom, transactionsRoot, stateRoot, difficulty,
    totalDifficulty, extraData, size, gasLimit, gasUsed, timestamp) <> (Block.tupled, Block.unapply)
  }

  val blocks = TableQuery[Blocks]

  class Transactions(tag: Tag) extends Table[Transaction](tag, "TRANSACTION") {
    def hash = column[TransactionHash]("HASH", O.PrimaryKey)
    def nonce = column[BigInt]("NONCE")
    def blockHash = column[BlockHash]("BLOCK_HASH")
    def transactionIndex = column[BigInt]("TRANSACTION_INDEX")
    def from = column[Address]("FROM")
    def to = column[Option[Address]]("TO")
    def value = column[BigInt]("VALUE")
    def gasPrice = column[BigInt]("GAS_PRICE")
    def gas = column[BigInt]("GAS")
    def input = column[ByteString]("INPUT")

    def * = (hash, nonce, blockHash, transactionIndex, from, to, value, gasPrice, gas, input) <> (Transaction.tupled, Transaction.unapply)

    def block = foreignKey("BLOCK_FK", blockHash, blocks)(_.hash, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  }

  val transactions = TableQuery[Transactions]

  class Accounts(tag: Tag) extends Table[Account](tag, "ACCOUNT") {
    def nonce = column[BigInt]("NONCE")
    def address = column[Address]("ADDRESS", O.PrimaryKey)
    def balance = column[BigInt]("BALANCE")

    def * = (nonce, address, balance) <> (Account.tupled, Account.unapply)
  }

  val accounts = TableQuery[Accounts]

  class Receipts(tag: Tag) extends Table[Receipt](tag, "RECEIPT") {
    def transactionHash = column[TransactionHash]("TRANSACTION_HASH")
    def cumulativeGasUsed = column[BigInt]("CUMULATIVE_GAS_USED")
    def gasUsed = column[BigInt]("GAS_USED")
    def contractAddress = column[Option[Address]]("CONTRACT_ADDRESS")
    def logsBloomFilter = column[ByteString]("LOGS_BLOOM_FILTER")
    def status = column[Option[Int]]("STATUS")

    def * = (transactionHash, cumulativeGasUsed, gasUsed, contractAddress, logsBloomFilter, status) <> (Receipt.tupled, Receipt.unapply)

    def transaction = foreignKey("TRANSACTION_FK", transactionHash, transactions)(_.hash, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  }

  val receipts = TableQuery[Receipts]

  class Logs(tag: Tag) extends Table[Log](tag, "LOG") {
    def transactionHash = column[TransactionHash]("TRANSACTION_HASH")
    def loggerAddress = column[Address]("LOGGER_ADDRESS", O.PrimaryKey)
    def topics = column[ByteString]("TOPICS")
    def data = column[ByteString]("DATA")

    def * = (transactionHash, loggerAddress, topics, data) <> (Log.tupled, Log.unapply)
  }

  val logs = TableQuery[Logs]

}
