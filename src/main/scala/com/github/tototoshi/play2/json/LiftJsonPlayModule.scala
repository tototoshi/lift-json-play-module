/*
 * Copyright 2012 Toshiyuki Takahashi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tototoshi.play2.json

import play.api._
import play.api.http._
import play.api.mvc._
import play.api.libs.iteratee._
import net.liftweb.json.{ JValue => LiftJValue, _ }
import scala.language.reflectiveCalls

trait LiftJsonWriteable { self: LiftJsonContentTypeOf =>

  implicit def writeableOf_LiftJValue(implicit codec: Codec): Writeable[LiftJValue] = {
    Writeable((jval: LiftJValue) => codec.encode((pretty(render(jval)))))
  }

}

trait LiftJsonContentTypeOf { self: LiftJsonWriteable =>

  implicit def contentTypeOf_JsValue(implicit codec: Codec): ContentTypeOf[LiftJValue] = {
    ContentTypeOf(Some(ContentTypes.JSON))
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


