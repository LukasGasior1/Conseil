package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString

object BlockHash {
  val Length = 32

  def apply(s: String): BlockHash = BlockHash(ByteUtils.decodeHex(s))
}

case class BlockHash(value: ByteString) {
  import BlockHash._

  require(value.length == Length, s"block hash should have exactly $Length bytes")

  override def toString = ByteUtils.toHexString(value)
}
