import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive
import akka.cluster.{ClusterEvent, Cluster}

object startCluster extends App {

  val system = ActorSystem ("cluster")
  system.actorOf(Props(new mainCluster))
  system.actorOf(Props(new clusterWorker))


}

class mainCluster extends Actor {
  val cluster = Cluster(context.system)
  cluster.subscribe(self, classOf[ClusterEvent.MemberUp])
  cluster.join(cluster.selfAddress)

  def receive = {
    case ClusterEvent.MemberUp(member) => println("member up")
  }
}

class clusterWorker extends Actor {
  val cluster = Cluster(context.system)
  cluster.subscribe(self, classOf[ClusterEvent.MemberRemoved])
  val main = cluster.selfAddress.copy(port = Some(2552))
  cluster.join(main)

  def receive = {
    case ClusterEvent.MemberRemoved(m, _) => if (m.address == main) context.stop(self)
  }
}