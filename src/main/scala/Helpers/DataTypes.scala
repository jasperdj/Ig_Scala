package Helpers

case class Client(id:Int)

case class LoadBalancer_Input(clientId:Int, benchmarkInput: BenchmarkInput)

case class Worker_Input(loadBalancer_Input: LoadBalancer_Input, loadbalancer_benchmarkoutput:BenchmarkOutput)
case class Worker_Output(output:String, nodeAddress:String, workerInput: Worker_Input, benchmarkOutput: BenchmarkOutput)

case class BenchmarkInput(nodeId: Int, forceExceptionChance: Double)
case class BenchmarkOutput(resourceUtil:Map[String, Double], unitPerformance:Map[String, Long])

//curl --data "{\"clientId\":1, \"benchmarkInput\": {\"nodeId\":4, \"forceExceptionChance\": 0.5}}" http://127.0.0.1:9000/ig_scala/singleClient