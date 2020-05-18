import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import spray.json._

case class Message(message: String)

trait MessageJsonProtocol extends DefaultJsonProtocol {
  implicit val messageFormat = jsonFormat1(Message)
}

object AkkaHttp extends App with MessageJsonProtocol with SprayJsonSupport {

  override def main(args: Array[String]) = {
    implicit val system = ActorSystem("LowLevelServerAPI")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher
    import akka.http.scaladsl.server.Directives._

    val helloRoute =
    {
      path("greet" / Segment) { name =>
        get {
          val msg = Message("Hello "+ name.capitalize)
          complete(msg)
        }
      } ~
      path("health") {
        complete(StatusCodes.OK)
      }
    }

    Http().bindAndHandle(helloRoute, "0.0.0.0", 8080)

    println("Listening on port 8080")
  }

}
