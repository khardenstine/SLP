package altitourny.slp

import events.SharedEventData
import log._
import java.io.File
import collection.mutable.HashMap
import org.joda.time.DateTime

class SLP(configLocation: String)
{
	private val log: Logger = new Logger("slp.log", LogLevel.DEBUG)

	private def run =
	{
		new ServerLogWatcher(new File("asdf"), false)
	}
}

object SLP
{
	private val slp: SLP = new SLP("config.properties")
	private val sharedEventData: HashMap[Int, SharedEventData] = HashMap.empty
	private var sessionStartTime: DateTime = null

	def main(args: Array[String])
	{
		slp.run
	}

	def getLog: Logger =
	{
		slp.log
	}

	def getSharedEventData(port: Int): SharedEventData =
	{
		sharedEventData.get(port).getOrElse(throw new RuntimeException("Server not started yet on port: " + port))
	}

	def initServer(port: Int) : SharedEventData =
	{
		val server: SharedEventData = new SharedEventData(sessionStartTime)
		sharedEventData.put(port, server)
		server
	}

	def startSession(dateTime: DateTime)
	{
		sharedEventData.clear()
		sessionStartTime = dateTime
	}

	def executeDBStatement(statement: String)
	{

	}

	def insertRawDBStatement(table: String, values: Seq[String])
	{
		executeDBStatement(
			"""
			  |INSERT INTO %s
			  |VALUES(%s)
			""".stripMargin.format(table, values.mkString(","))
		)
	}

	def insertDBStatement(table: String, values: Seq[Any])
	{
		executeDBStatement(
			"""
			  |INSERT INTO %s
			  |VALUES(%s)
			""".stripMargin.format(table, values.map("'" + _ + "'").mkString(","))
		)
	}
}
