import akka.actor.{Identify, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory

/**
  * Created by a623557 on 15-5-2016.
  */

object test2 extends App {

  args.foreach(println)
  val system = ActorSystem("test", ConfigFactory.load.getConfig("remoteSystem"))

  system.actorSelection("akka.tcp://WorkerSystem@raspberrypi.mshome.net:5152/user/manager") ! " "


}