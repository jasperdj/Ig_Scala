import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object futureTest extends App {
/*
  Future {
    1*2
  } onComplete {
    case Success(value) => Future {
      2*value
    } onComplete {
      case Success(value) => Future {
        println(3 * value)
      }
    }
  }

  println("value"+3*(2*(1*3)))
  /*
    future{3*future{2*future{1*2}}}

   */

  futureExample(5) onComplete {
    case Success(value) => futureExample(value) onComplete {
      case Success(value) => futureExample(value) onComplete {
        case Success(value) => println(value)
      }
    }
  }

  def futureExample(value:Int):Future[Int] = Future {
    5*value
  }


  Thread.sleep(5000)*/
}