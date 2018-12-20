package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString

case class Receipt(
    transactionHash: TransactionHash,
    cumulativeGasUsed: BigInt,
    gasUsed: BigInt,
    contractAddress: Option[Address],
    logsBloomFilter: ByteString,
    status: Option[Int])

case class Log(
    transactionHash: TransactionHash,
    loggerAddress: Address,
    topics: ByteString, // Seq[ByteString]
    data: ByteString)
