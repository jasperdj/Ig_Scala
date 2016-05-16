import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.cluster.Cluster
import akka.http.scaladsl.model.HttpResponse
import scala.util.{Failure, Success}
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout

/**
  * Created by a623557 on 27-4-2016.
  */
/*

case class jsonMessage(json:String)

object startTest extends App {


  implicit val httpSystem:ActorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher= httpSystem.dispatcher
  Http().bindAndHandle(route, "0.0.0.0", 9000)


  val route =
    path("hello") {
      get {
        complete {
          HttpResponse(entity = "Hi there")
        }
      }
    }
}

class Master extends Actor {
  var router = {
    val routees = Vector.fill(100) {
      val r = context.actorOf(Props[Routee])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case `jsonMessage` =>
      router.route(`jsonMessage`, sender())
  }
}

class Routee extends Actor with ActorLogging {
  def receive = {
    case `jsonMessage` ⇒ "yeas"
  }
}*/
