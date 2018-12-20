package tech.cryptonomic.conseil.ethereum

import akka.util.ByteString

case class Block(
    number: BigInt,
    hash: BlockHash,
    parentHash: BlockHash,
    sha3Uncles: ByteString,
    logsBloom: ByteString,
    transactionsRoot: ByteString,
    stateRoot: ByteString,
    difficulty: BigInt,
    totalDifficulty:  BigInt,
    extraData: ByteString,
    size: BigInt,
    gasLimit: BigInt,
    gasUsed: BigInt,
    timestamp: BigInt)
