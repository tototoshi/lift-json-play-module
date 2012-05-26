package com.github.tototoshi.play2.json

import play.api._
import play.api.http._
import play.api.mvc._
import play.api.libs.iteratee._
import net.liftweb.json.{ JValue => LiftJValue, _ }


trait LiftJsonWriteable {

  implicit def writeableOf_LiftJValue(implicit codec: Codec): Writeable[LiftJValue] = {
    Writeable[LiftJValue](jval => codec.encode((pretty(render(jval)))))
  }

}

trait LiftJsonContentTypeOf {

  implicit def contentTypeOf_JsValue(implicit codec: Codec): ContentTypeOf[LiftJValue] = {
    ContentTypeOf[LiftJValue](Some(ContentTypes.JSON))
  }

}

trait LiftJsonParser {

  def tolerantJson(maxLength: Int): BodyParser[LiftJValue] = BodyParser("json, maxLength=" + maxLength) { request =>
    play.api.libs.iteratee.Traversable.takeUpTo[Array[Byte]](maxLength).apply(Iteratee.consume[Array[Byte]]().map { bytes =>
      scala.util.control.Exception.allCatch[LiftJValue].either {
        JsonParser.parse(new String(bytes, request.charset.getOrElse("utf-8")))
      }.left.map { e =>
        (Play.maybeApplication.map(_.global.onBadRequest(request, "Invalid Json")).getOrElse(Results.BadRequest), bytes)
      }
    }).flatMap(Iteratee.eofOrElse(Results.EntityTooLarge))
    .flatMap {
      case Left(b) => Done(Left(b), Input.Empty)
      case Right(it) => it.flatMap {
        case Left((r, in)) => Done(Left(r), Input.El(in))
        case Right(json) => Done(Right(json), Input.Empty)
      }
    }
  }

  def tolerantJson: BodyParser[LiftJValue] = tolerantJson(BodyParsers.parse.DEFAULT_MAX_TEXT_LENGTH)

  def liftJson(maxLength: Int): BodyParser[LiftJValue] = BodyParsers.parse.when(
    _.contentType.exists(m => m == "text/json" || m == "application/json"),
    tolerantJson(maxLength),
    request => Play.maybeApplication.map(_.global.onBadRequest(request, "Expecting text/json or application/json body")).getOrElse(Results.BadRequest)
  )

  def liftJson: BodyParser[LiftJValue] = liftJson(BodyParsers.parse.DEFAULT_MAX_TEXT_LENGTH)

}

trait LiftJson extends LiftJsonParser with LiftJsonWriteable with LiftJsonContentTypeOf


