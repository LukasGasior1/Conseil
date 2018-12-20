package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString

case class Transaction(
    hash: TransactionHash,
    nonce: BigInt,
    blockHash: BlockHash,
    transactionIndex: BigInt,
    from: Address,
    to: Option[Address],
    value: BigInt,
    gasPrice: BigInt,
    gas: BigInt,
    input: ByteString)
