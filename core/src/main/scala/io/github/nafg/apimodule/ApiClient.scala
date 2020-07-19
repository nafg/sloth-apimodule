package io.github.nafg.apimodule

import cats.implicits._
import io.circe.Errors
import sloth._

import scala.concurrent.{ExecutionContext, Future}


abstract class ApiClient(implicit executionContext: ExecutionContext) {
  def send(path: String, payload: String): Future[String]

  def printError(message: String): Unit

  private class Prefixed(prefix: String) extends RequestTransport[String, Future] {
    override def apply(request: Request[String]): Future[String] =
      send("/_sloth_api/" + (prefix :: request.path).mkString("/"), request.payload)
  }

  object LogHandler extends LogHandler[Future] {
    override def logRequest[T](path: List[String], argumentObject: Product, result: Future[T]): Future[T] =
      result.recoverWith {
        case e@ClientException(ClientFailure.DeserializerError(Errors(nel))) =>
          printError(
            s"Could not deserialize API response for path $path with args $argumentObject: " +
              nel.toList.map(_.show).mkString(";  ")
          )
          Future.failed(e)
      }
  }

  def client(prefix: String): Client[String, Future, ClientException] = Client(new Prefixed(prefix), LogHandler)

  def apply(module: ApiModule): module.ClientApi = module.wirePrefixed(client)
}
