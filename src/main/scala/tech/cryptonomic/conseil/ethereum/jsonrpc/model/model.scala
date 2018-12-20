package tech.cryptonomic.conseil.ethereum.jsonrpc.model

import akka.util.ByteString
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JArray, JInt, JNull, JString}
import tech.cryptonomic.conseil.ethereum.{Address, BlockHash, ByteUtils, TransactionHash}

case class Block(
    number: Quantity,
    hash: BlockHash,
    parentHash: BlockHash,
    sha3Uncles: ByteString,
    logsBloom: ByteString,
    transactionsRoot: ByteString,
    stateRoot: ByteString,
    difficulty: Quantity,
    totalDifficulty:  Quantity,
    extraData: ByteString,
    size: Quantity,
    gasLimit: Quantity,
    gasUsed: Quantity,
    timestamp: Quantity,
    transactions: Seq[TransactionHash],
    uncles: Seq[BlockHash])

case class TraceFilterRequest(
    fromBlock: Option[Quantity],
    toBlock: Option[Quantity],
    fromAddress: Option[Seq[Address]],
    toAddress: Option[Seq[Address]])

case class Action(
    callType: Option[String],
    from: Option[Address],
    gas: Option[Quantity],
    input: Option[ByteString],
    to: Option[Address],
    value: Option[Quantity],
    address: Option[Address],
    balance: Option[Quantity],
    author: Option[Address],
    refundAddress: Option[Address])

case class Result(
    gasUsed: Quantity,
    output: ByteString)

case class Trace(
    action: Action,
    blockHash: BlockHash,
    blockNumber: Quantity,
    result: Option[Result],
    subtraces: Int,
    error: Option[String],
    traceAddress: Seq[Int],
    transactionHash: Option[TransactionHash],
    transactionPosition: Option[Int],
    `type`: String)

object Quantity {
  val serializer = new CustomSerializer[Quantity](format => ( {
    case JInt(n) => Quantity(n)
    case JString(s) => Quantity(BigInt(1, ByteUtils.decodeHex(s).toArray[Byte]))
    case other => throw new RuntimeException(s"Cannot decode quantity: $other")
  }, {
    case quantity: Quantity => JString(ByteUtils.toHexString(quantity.value))
  }))
}

case class Quantity(value: BigInt) {
  override def toString: String = value.toString
}

object LogsFilter {
  sealed trait Topic
  object Topic {
    val Length = 32

    case class AnyOf(arr: ByteString*) extends Topic
    case object Any extends Topic

    val serializer = new CustomSerializer[Topic](format => ( {
      case _ => throw new RuntimeException("Not supported")
    }, {
      case anyOf: AnyOf => JArray(anyOf.arr.map(h => JString(ByteUtils.toHexString(h))).toList)
      case Any => JNull
    }))
  }
}
case class LogsFilter(fromBlock: Option[Quantity], toBlock: Option[Quantity], address: Option[Address], topics: Seq[LogsFilter.Topic])

case class Log(
    logIndex: Quantity,
    transactionIndex: Quantity,
    transactionHash: TransactionHash,
    blockHash: BlockHash,
    blockNumber: Quantity,
    address: Address,
    data: ByteString,
    topics: Seq[ByteString])

case class CallData(
    from: Option[Address],
    to: Option[Address],
    gas: Option[BigInt],
    gasPrice: Option[BigInt],
    value: Option[BigInt],
    data: ByteString)

object BlockParam {
  case object Latest extends BlockParam
  case object Pending extends BlockParam
  case class WithNumber(n: BigInt) extends BlockParam

  val serializer = new CustomSerializer[BlockParam](format => ( {
    case _ => throw new RuntimeException("Not supported")
  }, {
    case Pending => JString("pending")
    case Latest => JString("latest")
    case WithNumber(n) => JString(ByteUtils.toHexString(n))
  }))
}
sealed trait BlockParam

case class TransactionResponse(
    hash: TransactionHash,
    nonce: Quantity,
    blockHash: Option[BlockHash],
    blockNumber: Option[Quantity],
    transactionIndex: Option[Quantity],
    from: Address,
    to: Option[Address],
    value: Quantity,
    gasPrice: Quantity,
    gas: Quantity,
    input: ByteString,
    pending: Option[Boolean],
    isOutgoing: Option[Boolean])

case class TransactionReceiptResponse(
    transactionHash: TransactionHash,
    transactionIndex: Quantity,
    blockNumber: Quantity,
    blockHash: BlockHash,
    cumulativeGasUsed: Quantity,
    gasUsed: Quantity,
    contractAddress: Option[Address],
    logs: Seq[TxLog],
    status: Option[Quantity],
    returnData: Option[ByteString])

case class TxLog(
    logIndex: Quantity,
    transactionIndex: Quantity,
    transactionHash: TransactionHash,
    blockHash: BlockHash,
    blockNumber: Quantity,
    address: Address,
    data: ByteString,
    topics: Seq[ByteString])
