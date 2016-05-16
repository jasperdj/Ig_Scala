package Helpers

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.sys.process._
import scala.util.{Failure, Success}

/**
  * Created by a623557 on 8-4-2016.
  */

case class update()
case class currentLoad()

class CPU_Monitor extends Actor {
  var latestCpuLoad = -1.0
  var latestRamUsed = -1.0

  override def receive: Receive = {
    case `update` => updateCPU
    case `currentLoad` => sender ! Map("cpuLoad" -> latestCpuLoad, "ramUsed" -> latestRamUsed)
    case _ => println("Error: Did not expect that!")
  }

  def updateCPU: Unit = {
    val gettingCpuStatistics:Future[String] = Future {
      "top -bn2 "!!
    }

    gettingCpuStatistics.onComplete {
      case Success(cpuResult) => processCPUResult(cpuResult)
      case Failure(t) => println("An error has occured: " + t.getMessage)
    }
  }

  def processCPUResult(resourceResult:String): Unit = {
    val cpuExpression = "%Cpu\\(s\\): +(\\d+\\.\\d+) us, +(\\d+\\.\\d+)".r
    val latestUserCpuLoad = cpuExpression.findAllMatchIn(resourceResult).map(_.group(1)).toList(1).toDouble
    val latestSystemCpuLoad = cpuExpression.findAllMatchIn(resourceResult).map(_.group(2)).toList(1).toDouble
    latestCpuLoad = latestUserCpuLoad + latestSystemCpuLoad

    val ramExpression = "KiB Mem: +(\\d+) total, +(\\d+)".r
    latestRamUsed = ramExpression.findAllMatchIn(resourceResult).map(_.group(2)).toList.head.toDouble
  }
}