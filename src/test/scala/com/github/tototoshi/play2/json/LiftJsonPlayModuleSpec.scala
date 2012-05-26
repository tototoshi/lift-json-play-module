package com.github.tototoshi.play2.json

import org.specs2.mutable._

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import net.liftweb.json._

case class Person(id: Long, name: String, age: Int)

object TestApplication extends Controller with LiftJson {

  implicit val formats = DefaultFormats

  def get = Action { implicit request =>
    Ok(Extraction.decompose(Person(1, "ぱみゅぱみゅ", 19)))
  }

  def post = Action(liftJson) { implicit request =>
    Ok(request.body.extract[Person].name)
  }

}


class LiftJsonPlayModuleSpec extends Specification with LiftJson {

  "LiftJsonPlayModule" should {

    "allow you to use lift-json value as response" in {

      def removeWhiteSpaceAndNewLine(s: String): String = s.replace(" ", "").replace("\n", "")

      val res = TestApplication.get(FakeRequest("GET", ""))

      contentType(res) must beEqualTo (Some("application/json"))
      removeWhiteSpaceAndNewLine(contentAsString(res)) must beEqualTo ("""{"id":1,"name":"ぱみゅぱみゅ","age":19}""")

    }

    "accept lift json request" in {
      val res = TestApplication.post(FakeRequest("POST", "", FakeHeaders(Map("Content-Type" -> Seq("application/json"))), parse("""{"id":1,"name":"ぱみゅぱみゅ","age":19}""")))
      contentAsString(res) must beEqualTo ("ぱみゅぱみゅ")
    }

  }

}

