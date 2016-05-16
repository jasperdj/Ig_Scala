package LoadBalancer

import Helpers._
import WorkerNode.Manager
import akka.actor._
import akka.routing.RoundRobinGroup
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global



/**
  * Created by a623557 on 28-4-2016.
  */

case class jsonMessagee(messageType:String, json:String)
case class setId(id:Int)


object Distributor {
  //Setting up the actor system.
  implicit val system = HttpService.system
  implicit val materializer = HttpService.materializer

  val resourceMonitor = system.actorOf(Props(new CPU_Monitor))

  val isUnix = System.getProperty("os.name") != "Windows 7"
  if (isUnix)
    system.scheduler.schedule(0 seconds, 5 seconds, resourceMonitor, update)

  private val remoteWorkers = scala.collection.immutable.Iterable(
    "akka.tcp://WorkerSystem@192.168.137.77:5152/user/manager") //akka.tcp://actorSystem@127.0.0.1:5151/user/worker1

  val router: ActorRef =
    system.actorOf(RoundRobinGroup(remoteWorkers).props())
}






/*
TODAY: Setup routing & testing.


Serialize, Http call(random)
  Get from Web service simulation
    Send client-ID
    Schedule future
    Generate random data list of numbers and strings
    Save data in DB
  Event when selection of futures are done -- all data is gathered.
  Send to webservice simulation.
*/
