package unitTests




import Helpers.Database

import scala.concurrent.Await
import scala.concurrent.duration._


object run extends App {
  new databaseTest
}

class databaseTest {
  def assertt(value: Boolean): Unit = {
    if (!value) throw new Error("Assertion failed")
  }

  val id = "testId"


  val insert = Database.insertData(id, "{\n  \"id\": 4,\n  \"name\": \"Patricia Lebsack\",\n  \"username\": \"Karianne\",\n  \"email\": \"Julianne.OConner@kory.org\",\n  \"address\": {\n    \"street\": \"Hoeger Mall\",\n    \"suite\": \"Apt. 692\",\n    \"city\": \"South Elvis\",\n    \"zipcode\": \"53919-4257\",\n    \"geo\": {\n      \"lat\": \"29.4572\",\n      \"lng\": \"-164.2990\"\n    }\n  },\n  \"phone\": \"493-170-9623 x156\",\n  \"website\": \"kale.biz\",\n  \"company\": {\n    \"name\": \"Robel-Corkery\",\n    \"catchPhrase\": \"Multi-tiered zero tolerance productivity\",\n    \"bs\": \"transition cutting-edge web services\"\n  }\n}")
  val result = Await.result(insert, 1 minute)
  assertt(!result.hasErrors)


  {
    val count = Database.getAmountOfDataObjects(id)
    val countResult = Await.result(count, 1 minute)
    println(countResult)
    assertt(countResult == 1)

  }

  {
  val remove = Database.removeAllDataObject(id)
  val result = Await.result(remove, 100 seconds)
  assertt(!result.hasErrors)
}

  {
    val count = Database.getAmountOfDataObjects(id)
    val totalCount = Await.result(count, 1 minute)
    assertt(totalCount == 0)
  }
}
