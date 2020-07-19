package io.github.nafg.apimodule

import io.circe.{Decoder, Encoder, Json}
import magnolia._


class ParamEncoder[T](val underlying: Encoder[T]) extends AnyVal

trait ParamEncoderLowPriority {
  type Typeclass[T] = ParamEncoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    new Typeclass(Encoder.instance { value =>
      Json.obj(caseClass.parameters.map(p => p.label -> p.typeclass.underlying(p.dereference(value))): _*)
    })

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

object ParamEncoder extends ParamEncoderLowPriority {
  implicit def default[A](implicit A: Encoder[A]): Typeclass[A] = new Typeclass(A)
}

class ParamDecoder[T](val underlying: Decoder[T]) extends AnyVal

trait ParamDecoderLowPriority {
  type Typeclass[T] = ParamDecoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    new Typeclass(Decoder.instance { cursor =>
      caseClass.constructEither(param => cursor.get(param.label)(param.typeclass.underlying)).left.map(_.head)
    })

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

object ParamDecoder extends ParamDecoderLowPriority {
  implicit def default[A](implicit A: Decoder[A]): Typeclass[A] = new Typeclass(A)
}
