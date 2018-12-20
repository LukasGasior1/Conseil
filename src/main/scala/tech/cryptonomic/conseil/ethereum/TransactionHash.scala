package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString

object TransactionHash {
  val Length = 32

  def apply(s: String): TransactionHash = TransactionHash(ByteUtils.decodeHex(s))
  def apply(arr: Array[Byte]): TransactionHash = TransactionHash(ByteString(arr))
}

case class TransactionHash(value: ByteString) {
  import TransactionHash._

  require(value.length == Length, s"transaction hash should have exactly $Length bytes")

  override def toString = ByteUtils.toHexString(value)
}
