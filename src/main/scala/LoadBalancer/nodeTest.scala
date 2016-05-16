package LoadBalancer

import Helpers.{Worker_Input, Worker_Output}
import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class MasterNode extends Actor {
  implicit val timeout = Timeout(30 seconds)
  var firstSender:ActorRef = null

  val receive:Receive = {
    case m:Worker_Input =>
      val worker = context.actorOf(Props(new workerNode))
      val future = worker ? m
      firstSender = sender
      future onComplete {
        case Success(m) => firstSender ! m
      }
  }
}

class workerNode extends Actor {

  implicit val materializer = HttpService.materializer

  def sendParralelApiRequests(amount:Int, user:Int): Unit = {
    for (i <- 1 to amount) {
      val future = Http(context.system).singleRequest(HttpRequest(uri = s"http://jsonplaceholder.typicode.com/users/$user"))
      future onComplete {
        case Success(e) => println("Succes: " + e.entity)
        case Failure(e) => println("Failure: "+ e)
      }
    }
  }

  val receive:Receive = {

    case m:Worker_Input =>
      println("worker received")
      sendParralelApiRequests(2, 1)
      sender ! Worker_Output(s"Output succesfull from $self", null, null, null)
      context.stop(self)
  }
}