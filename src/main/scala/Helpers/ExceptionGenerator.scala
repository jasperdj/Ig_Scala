package Helpers

object ExceptionGenerator {
  private def random = scala.util.Random

  def Try(chance:Double):Unit = {
    if (random.nextInt(100) < chance * 100) throw new Error("Forced exception")
  }
}