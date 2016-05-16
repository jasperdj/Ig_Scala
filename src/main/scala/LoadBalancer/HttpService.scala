package LoadBalancer


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory


object HttpService extends App with Routes {
  implicit val system:ActorSystem = ActorSystem("HttpSystem", ConfigFactory.load.getConfig("remoteSystem"))

  implicit val materializer = ActorMaterializer()

  implicit val dispatcher= system.dispatcher

  Http().bindAndHandle(routes, "0.0.0.0", 9000)

  println("Server up and running...")
}