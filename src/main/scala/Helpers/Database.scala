package Helpers
import reactivemongo.util.LazyLogger
import reactivemongo.api._
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.BSONDocument
import reactivemongo.api.collections.bson.BSONCollection

object Database {

  val collection = connect()

  def connect(): BSONCollection = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost"))

    val db = connection("Ig_scala")
    db.collection("ActorData")
  }

  def insertData(id:String, dataObject:String) : Future[WriteResult] = {
    val query = BSONDocument("id" -> id, "dataObject" -> dataObject)
    Database.collection.insert(query)
  }

  def getAmountOfDataObjects(id:String): Future[Int] = {
    val getAmount = Count(BSONDocument("id" -> id))
    val resultAmount: Future[CountResult] = collection.runCommand(getAmount)

    for{
      amount <- resultAmount
    } yield amount.value
  }

  def removeAllDataObject(id:String) : Future[WriteResult] = {
    collection.remove(BSONDocument("id" -> id))
  }

  def getDataObjects(id:String) : Future[Option[BSONDocument]] = {
    collection.find(BSONDocument("id" -> id)).one[BSONDocument]
  }

  println("Test succesfull")
}