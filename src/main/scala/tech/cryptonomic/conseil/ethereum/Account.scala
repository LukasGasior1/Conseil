package tech.cryptonomic.conseil.ethereum

case class Account(
    nonce: BigInt,
    address: Address,
    balance: BigInt)
