# lift-json module for Play20


This module allows you to use lift-json in your play20 application.


## Usage


```scala

import com.github.tototoshi.play2.json.LiftJson
import net.liftweb.json._

case class Person(id: Long, name: String, age: Int)

object Application extends Controller with LiftJson {

  implicit val formats = DefaultFormats

  def get = Action { implicit request =>
    Ok(Extraction.decompose(Person(1, "ぱみゅぱみゅ", 19)))
  }

  def post = Action(liftJson) { implicit request =>
    Ok(request.body.extract[Person].name)
  }

}
```
