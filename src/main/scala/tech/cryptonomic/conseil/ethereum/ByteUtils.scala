package tech.cryptonomic.conseil.ethereum

import java.nio.ByteBuffer

import akka.util.ByteString
import org.spongycastle.util.encoders.Hex

object ByteUtils {

  def decodeHex(s: String): ByteString = {
    val stripped = s.replaceFirst("^0x", "")
    val normalized = if (stripped.length % 2 == 1) "0" + stripped else stripped
    ByteString(Hex.decode(normalized))
  }

  def toHexString(input: ByteString): String =
    s"0x${Hex.toHexString(input.toArray[Byte])}"

  def toHexString(input: BigInt): String =
    s"0x${input.toString(16)}"

  def leftPadTo(hex: ByteString, len: Int, elem: Byte): ByteString = {
    hex.reverse.padTo(len, elem).reverse
  }

  def bytesToInts(bytes: ByteString): Array[Int] = {
    bytes
      .toArray[Byte]
      .reverse
      .grouped(4)
      .toArray
      .reverse
      .map { arr =>
        if (arr.length == 4) arr.reverse
        else Array.fill(4 - arr.length)(0x00.toByte) ++ arr.reverse
      }
      .map(ByteBuffer.wrap(_).getInt)
  }

  def bigIntToUnsignedByteArray(bigInt: BigInt): Array[Byte] = {
    val asByteArray = bigInt.toByteArray
    if (asByteArray.head == 0) asByteArray.tail
    else asByteArray
  }

}
