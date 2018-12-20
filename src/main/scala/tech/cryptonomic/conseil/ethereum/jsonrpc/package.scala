package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

package object jsonrpc {

  val baseSerializers = {
    val AddressSerializer = new CustomSerializer[Address](formats => ( {
      case JString(str) => Address(str)
    }, {
      case address: Address => JString(address.toString)
    }))

    val BlockHashSerializer = new CustomSerializer[BlockHash](formats => ( {
      case JString(str) => BlockHash(str)
    }, {
      case blockHash: BlockHash => JString(blockHash.toString)
    }))

    val TransactionHashSerializer = new CustomSerializer[TransactionHash](formats => ( {
      case JString(str) => TransactionHash(str)
    }, {
      case transactionHash: TransactionHash => JString(transactionHash.toString)
    }))

    val ByteStringSerializer = new CustomSerializer[ByteString](format => ( {
      case JString(s) => ByteUtils.decodeHex(s)
      case other => throw new RuntimeException(s"Cannot decode ByteString: $other")
    }, {
      case bs: ByteString => JString(ByteUtils.toHexString(bs))
    }))

    Seq(AddressSerializer, BlockHashSerializer, TransactionHashSerializer, ByteStringSerializer)
  }

}
