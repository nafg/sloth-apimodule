package io.github.nafg.apimodule

import sloth.{Request, RouterResult, ServerFailure}


object ApiRouter {
  def apply[F[_]](routes: PartialFunction[Request[String], RouterResult[String, F]]*
                 ): PartialFunction[Request[String], Either[ServerFailure, F[String]]] = {
    routes.reduce(_ orElse _)
      .andThen(_.toEither)
      .orElse[Request[String], Either[ServerFailure, F[String]]] {
        case req => Left(ServerFailure.PathNotFound(req.path))
      }
  }
}
