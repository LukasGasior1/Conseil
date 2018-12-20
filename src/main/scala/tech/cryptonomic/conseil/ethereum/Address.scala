package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString

object Address {
  val Length = 20

  def apply(s: String): Address = Address(ByteUtils.decodeHex(s))
}

case class Address(value: ByteString) {
  import Address._

  require(value.length == Length, s"address should have exactly $Length bytes")

  override def toString = ByteUtils.toHexString(value)
}
