import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object test extends App {

  implicit val system = ActorSystem("actorSystem")
  implicit val materializer = ActorMaterializer()

  def sendSequentialApiRequests(amount:Int, user:Int):Unit = {
    def iterator(amount:Int, list:List[Future[HttpResponse]]): List[Future[HttpResponse]] = {
      println("iterate")
      if (amount == 0) list
      else {
        val responseFuture: Future[HttpResponse] =
          Http(system).singleRequest(HttpRequest(uri = s"http://jsonplaceholder.typicode.com/users/$user"))
        iterator(amount-1, list ++ List(responseFuture))
      }
    }

    val fetchingUserData = Future.sequence( iterator(10,List()) )
    //println("result waiting")
    val result2 = Await.result(fetchingUserData, 1 minute)

    println("result: " + result2.toString)
  }
  println("busy")
  sendSequentialApiRequests(1, 1)


  /*def generateApiRequests(amount:Int, user:Int):List[Future[HttpResponse]] = {
    def iterator(amount:Int, list:List[Future[HttpResponse]]): List[Future[HttpResponse]] = {
      if (amount == 0) list
      else {
        val responseFuture: Future[HttpResponse] =
          Http().singleRequest(HttpRequest(uri = s"http://jsonplaceholder.typicode.com/users/$user"))
        iterator(amount-1, list ++ List(responseFuture))
      }
    }
    iterator(amount, List())
  }
*/

/*
  fetchingUserData onComplete {
    case Success(data) =>
      for(httpRequest:HttpResponse <- data) {
        println(httpRequest.entity)
      }
      println(s"totalTime = ${System.currentTimeMillis() - time1}")
    case Failure(e) => println(e)
  }*/
}