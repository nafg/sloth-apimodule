package io.github.nafg.apimodule

import cats.MonadError
import chameleon.{Deserializer, Serializer}
import io.circe.{HCursor, parser}
import sloth.{Client, ClientException, Request, Router, RouterResult}

import scala.concurrent.{ExecutionContext, Future}


trait ApiModule {
  type ServerResult[T]

  type Api[F[_]]

  type ServerApi = Api[ServerResult]
  type ClientApi = Api[Future]

  protected implicit def circeSerializer[T](implicit T: ParamEncoder[T]): Serializer[T, String] =
    T.underlying(_).noSpaces

  protected implicit def circeDeserializer[T](implicit T: ParamDecoder[T]): Deserializer[T, String] =
    parser.parse(_).flatMap(json => T.underlying(HCursor.fromJson(json)))

  protected implicit def monadError(implicit executionContext: ExecutionContext): MonadError[Future, Throwable] =
    cats.instances.future.catsStdInstancesForFuture

  protected def route(implicit c: ExecutionContext
                     ): (Router[String, ServerResult], ServerApi) => Router[String, ServerResult]

  protected def wire: Client[String, Future, ClientException] => ClientApi

  val prefix: String = getClass.getName

  def wirePrefixed(f: String => Client[String, Future, ClientException]): ClientApi = wire(f(prefix))

  def routeAlone(apiImpl: ServerApi)(implicit executionContext: ExecutionContext): Router[String, ServerResult] =
    route.apply(Router[String, ServerResult], apiImpl)

  def routePrefixed(apiImpl: ServerApi)
                   (implicit executionContext: ExecutionContext
                   ): PartialFunction[Request[String], RouterResult[String, ServerResult]] = {
    case Request(`prefix` :: rest, payload) => routeAlone(apiImpl).apply(Request(rest, payload))
  }
}
