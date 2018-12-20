package tech.cryptonomic.conseil.ethereum.jsonrpc

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.util.ByteString
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods
import org.json4s.native.JsonMethods.{compact, render}
import org.json4s.{DefaultFormats, Extraction, native}
import tech.cryptonomic.conseil.ethereum.{Address, BlockHash, ByteUtils, TransactionHash}
import tech.cryptonomic.conseil.ethereum.jsonrpc.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success, Try}

object JsonRpcClient {
  private case class Request(jsonrpc: String, method: String, params: Option[JArray], id: Option[JValue])
  private case class Response[Result](jsonrpc: String, result: Option[Result], error: Option[Error], id: JValue)
  private case class Error(code: Int, message: String, data: Option[JValue])

  class NoResultInResponseException extends RuntimeException("JSON RPC did not return an error or result")
}

class JsonRpcClient(config: JsonRpcClientConfig)
                   (implicit system: ActorSystem, materializer: Materializer, ec: ExecutionContext) {

  import JsonRpcClient._

  private implicit val serialization = native.Serialization
  private implicit val formats = DefaultFormats.preservingEmptyValues ++
    baseSerializers +
    Quantity.serializer +
    LogsFilter.Topic.serializer +
    BlockParam.serializer

  def getBlockByNumber(number: BigInt): Future[Block] = {
    postRequest[Block](method = "eth_getBlockByNumber", params = Seq(ByteUtils.toHexString(number), false))
  }

  def getBlockByHash(hash: BlockHash): Future[Block] = {
    postRequest[Block](method = "eth_getBlockByHash", params = Seq(hash.toString, false))
  }

  def getBlockNumber(): Future[BigInt] = {
    postRequest[Quantity](method = "eth_blockNumber", params = Nil)
      .map(_.value)
  }

  def getLogs(filter: LogsFilter): Future[Seq[Log]] = {
    postRequest[Seq[Log]](method = "eth_getLogs", params = Seq(Extraction.decompose(filter)))
  }

  def traceFilter(fromBlock: Option[BigInt] = None,
                  toBlock: Option[BigInt] = None,
                  fromAddress: Option[Seq[Address]] = None,
                  toAddress: Option[Seq[Address]] = None): Future[Seq[Trace]] = {
    postRequest[Seq[Trace]](
      method = "trace_filter",
      params = Seq(Extraction.decompose(TraceFilterRequest(
        fromBlock = fromBlock.map(Quantity.apply),
        toBlock = toBlock.map(Quantity.apply),
        fromAddress = fromAddress,
        toAddress = toAddress))))
  }

  def call(callData: CallData, blockParam: BlockParam): Future[ByteString] = {
    postRequest[ByteString](method = "eth_call", params = Seq(Extraction.decompose(callData), Extraction.decompose(blockParam)))
  }

  def getTransactionCount(address: Address, blockParam: BlockParam): Future[BigInt] = {
    postRequest[Quantity](method = "eth_getTransactionCount", params = Seq(address.toString, Extraction.decompose(blockParam)))
      .map(_.value)
  }

  def getBalance(address: Address, blockParam: BlockParam): Future[BigInt] = {
    postRequest[Quantity](method = "eth_getBalance", params = Seq(address.toString, Extraction.decompose(blockParam)))
      .map(_.value)
  }

  def getTransactionByHash(transactionHash: TransactionHash): Future[TransactionResponse] = {
    postRequest[TransactionResponse](method = "eth_getTransactionByHash", params = Seq(transactionHash.toString))
  }

  def getTransactionReceipt(transactionHash: TransactionHash): Future[Option[TransactionReceiptResponse]] = {
    postRequest[TransactionReceiptResponse](method = "eth_getTransactionReceipt", params = Seq(transactionHash.toString))
      .map(Some.apply)
      .recover { case _: NoResultInResponseException => None }
  }

  private def postRequest[Result](method: String, params: Seq[JValue])(implicit mf: Manifest[Response[Result]]): Future[Result] = {
    val jsonStr = compact(render(prepareJsonRequest(method, params)))
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = config.uri,
      entity = HttpEntity.Strict(ContentTypes.`application/json`, ByteString(jsonStr.getBytes("UTF-8"))))
    Http().singleRequest(request).flatMap(handleResponse[Result])
  }

  private def handleResponse[Result](httpResponse: HttpResponse)(implicit manifest: Manifest[Response[Result]]): Future[Result] = {
    if (httpResponse.status.isSuccess())
      Unmarshal(httpResponse.entity).to[String]
        .flatMap { responseStr =>
          val responseTry = Try(JsonMethods.parse(responseStr).extract[Response[Result]])
          responseTry match {
            case Success(res) if res.error.isEmpty =>
              res.result.map(Future.successful).getOrElse(Future.failed(new NoResultInResponseException))
            case Success(res) =>
              Future.failed(new RuntimeException(s"JSON RPC returned an error: ${res.error}"))
            case Failure(ex) =>
              Future.failed(new RuntimeException("Cannot read response json: " + responseStr, ex))
          }
        }
    else {
      httpResponse.discardEntityBytes().future().transform { _ =>
        Failure(new RuntimeException("Server did not return success status code"))
      }
    }
  }

  private def prepareJsonRequest(method: String, params: Seq[JValue]): JObject = {
    JObject(
      "jsonrpc" -> JString("2.0"),
      "method" -> JString(method),
      "params" -> JArray(params.toList),
      "id" -> JString(Random.nextInt(100000).toString))
  }

}
