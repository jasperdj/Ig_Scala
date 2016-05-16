package WorkerNode

import Helpers._
import LoadBalancer.{timeHelper, Distributor, HttpService, Routes}
import akka.actor.{ActorRef, Actor}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.Http
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.pipe
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.pattern.ask
import scala.concurrent.duration._

/**
  * Created by a623557 on 6-5-2016.
  */
case class seqResponse(response:Future[HttpResponse])

class workerNode extends Actor {
  val t1 = timeHelper.time
  var t2:Long = 0
  var t3:Long = 0

  var senderNode:ActorRef = null
  var responsesReceived = 0
  var worker_Input:Worker_Input = null

  val sequentialApiCalls = 5
  val parallelApiCalls = 5
  val amountOfDataObjectRequired = sequentialApiCalls + parallelApiCalls

  implicit val materializer = startManager.materializer
  implicit val timeout = Timeout(5 minutes)


  def sendParralelApiRequests(amount:Int, user:Int): Unit = {
    for (i <- 1 to amount) {
      Http(context.system).singleRequest(HttpRequest(uri = s"http://jsonplaceholder.typicode.com/users/$user"))
        .pipeTo(self)
    }
  }

  def sendSequentialApiRequests(amount:Int, user:Int):Unit = {
    def iterator(amount:Int, list:List[Future[HttpResponse]]): List[Future[HttpResponse]] = {
      if (amount == 0) list
      else {
        val responseFuture: Future[HttpResponse] =
          Http(context.system).singleRequest(HttpRequest(uri = s"http://jsonplaceholder.typicode.com/users/$user"))
        iterator(amount-1, list ++ List(responseFuture))
      }
    }
    val fetchingUserData = Future.sequence(iterator(amount, List()))
    fetchingUserData.pipeTo(self)
  }

  def sendOkBack = {
    println("ok")
    val t4 = timeHelper.time
    val getCurrentLoad:Future[Any] = startManager.resourceMonitor ? currentLoad

    getCurrentLoad onSuccess {
      case currentLoad:Map[String, Double] =>
        println("SUCCESS GETCURRENTLOAD")
        senderNode ! Worker_Output(s"recieved from clientid + ${worker_Input.loadBalancer_Input.clientId}",self.toString(),
          worker_Input, BenchmarkOutput(currentLoad.asInstanceOf[Map[String, Double]],
            timeHelper.createUnitPerformance(List(t1, t2, t3, t4, timeHelper.time))))
    }

    getCurrentLoad onFailure {
      case t => println("getCurrentLoad faiilure: " + t)
    }
  }

  def clasifyData = {
    println("clasifying")
    if (responsesReceived == amountOfDataObjectRequired) sendOkBack
  }

  def sendToProcessingServer = {
    val dataCollected = Database.getAmountOfDataObjects(self.toString)
    dataCollected onComplete {
      case Success(i) => if (i == amountOfDataObjectRequired) clasifyData
      case Failure(e) => println(e)
    }
  }

  def receive:Receive = {
    case w:Worker_Input => {
      t2 = timeHelper.time
      println("worker received")
      sendSequentialApiRequests(sequentialApiCalls, w.loadBalancer_Input.clientId)
      sendParralelApiRequests(parallelApiCalls, w.loadBalancer_Input.clientId)

      senderNode = sender()
      worker_Input = w
      t3 = timeHelper.time
    }
    case s:HttpResponse => responsesReceived += 1
      //Database.insertData(self.toString, s.toString)
      clasifyData
    case s:List[HttpResponse] => responsesReceived += s.size
    case _ => println("Worker node did not understand the message: ")
  }
}