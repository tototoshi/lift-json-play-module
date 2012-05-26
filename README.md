# lift-json module for Play20


This module allows you to use lift-json in your play20 application.


## Usage

```scala
// your play20 project settings

val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  resolvers += "tototoshi.github.com maven-repo/releases" at "http://tototoshi.github.com/maven-repo/releases",
  libraryDependencies ++= Seq(
    "com.github.tototoshi" %% "lift-json-play-module" % "0.1"
  )
)

```


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
