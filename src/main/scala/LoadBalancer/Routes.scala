package LoadBalancer

import Helpers._
import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object timeHelper {
  def createUnitPerformance(list:List[Long]):Map[String, Long] = {
    def iterator(list:List[Long], map:Map[String, Long]):Map[String, Long] =
    {
      list match {
        case x :: Nil => map
        case x :: tail => iterator(list.drop(1), map.updated("main_Unit"+map.size,  tail.head - x))
      }
    }
    iterator(list, Map())
  }

  def time = System.currentTimeMillis()
}

trait Routes extends JsonHelper {

  implicit val timeout:Timeout = Timeout(5 minutes)
  val routes = {
    path("ig_scala" / "singleClient") {
      post {
        entity(as[String]) { json =>
          val t1 = timeHelper.time
          val input = parse(json).extract[LoadBalancer_Input]
          val t2 = timeHelper.time
          val getCurrentLoad:Future[Any] = Distributor.resourceMonitor ? currentLoad
          val t3 = timeHelper.time
          onComplete(getCurrentLoad) {
            case Success(currentLoad) =>
              val routerCall = Distributor.router ? Worker_Input(input,
                BenchmarkOutput(currentLoad.asInstanceOf[Map[String, Double]], timeHelper.createUnitPerformance(List(t1, t2, t3, timeHelper.time))))
              onComplete(routerCall) {
                case Success(response) => complete {
                  HttpResponse(StatusCodes.OK, entity = write(response.asInstanceOf[Worker_Output]))
                }
                case Failure(e) => complete {
                  println("Route error - 1 : " + e)
                  HttpResponse(StatusCodes.InternalServerError)
                }
              }
          }
        }
      }
    }
  }
}

//curl --data "{\"totalExceptions\":0 ,\"id\":5 }" http://127.0.0.1:9000/bk_scala_up/update
//curl --data "{\"spaceId\":5, \"messageId\": 5, \"eventType\": 5, \"nodeId\":1, \"forceException\": false}" http://raspberrypi.mshome.net:9000/bk_scala/insert
//curl --data "{\"spaceId\":5, \"nodeId\":1, \"forceException\": false}" http://127.0.0.1:9000/bk_scala/spaceStatistics

//curl --data "{\"space"\}"

