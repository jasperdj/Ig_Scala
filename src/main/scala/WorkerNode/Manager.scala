package WorkerNode

import Helpers._
import akka.actor._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by a623557 on 6-5-2016.
  */

class Manager extends Actor {
  var firstSender:ActorRef = null

  implicit val timeout = Timeout(1 minute)
  val receive:Receive = {
    case w:Worker_Input =>
      val worker = context.actorOf(Props(new workerNode))
      val future = worker ? w
      firstSender = sender
      future onComplete {
        case Success(m) =>
          firstSender ! m
          context.stop(worker)
        case Failure(e) =>
          println(e)
          context.stop(worker)
      }

    case _ => println(self + " did not understand the message.")
  }
}

object startManager extends App {
  //Setup actor system
  implicit var system:ActorSystem = null
  if (args.length != 0) {
    system = ActorSystem("WorkerSystem", ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=${args(0)}").withFallback(ConfigFactory.load.getConfig("remoteSystem2")))
  } else {
    system = ActorSystem("WorkerSystem", ConfigFactory.load.getConfig("remoteSystem2"))
  }

  implicit val materializer = ActorMaterializer()
  val manager = system.actorOf(Props(new Manager), "manager")
  implicit val timeout = Timeout(1 minute)

  //Load resource monitor
  val resourceMonitor = system.actorOf(Props(new CPU_Monitor), "monitor")
  val isUnix = System.getProperty("os.name") != "Windows 7"
  if (isUnix)
    system.scheduler.schedule(0 seconds, 5 seconds, resourceMonitor, update)

  println("Manager up and running...")

}