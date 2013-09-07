package altitourney

import org.scalatest.FunSuite

class NonTests extends FunSuite{
	/*
	test("unique types") {
		val logFile = io.Source.fromFile("in/log.txt")

		val types = logFile.getLines().map {
			line =>
				(play.api.libs.json.Json.parse(line) \ "type").asOpt[String].getOrElse("_")
		}.toSeq.distinct.sorted.mkString("\n")

		logFile.close()

		println(types)
	}

	test("unique console commands") {
		val logFile = io.Source.fromFile("in/log.txt")

		val types = logFile.getLines()
			.filter( line => (play.api.libs.json.Json.parse(line) \ "type").as[String] == "consoleCommandExecute")
			.map(line => (play.api.libs.json.Json.parse(line) \ "command").as[String])
			.toSeq.distinct.sorted.mkString("\n")

		logFile.close()

		println(types)
	}
	*/
}
