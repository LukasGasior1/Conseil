package tech.cryptonomic.conseil.ethereum.jsonrpc

import com.typesafe.config.Config

case class JsonRpcClientConfig(uri: String)

object JsonRpcClientConfig {
  def apply(jsonRpcConfig: Config): JsonRpcClientConfig = {
    JsonRpcClientConfig(
      uri = jsonRpcConfig.getString("uri"))
  }
}
