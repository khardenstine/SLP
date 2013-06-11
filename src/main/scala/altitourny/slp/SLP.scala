package altitourny.slp

import com.typesafe.config.{ConfigFactory, Config}
import java.io.File
import java.sql.{SQLException, Connection, DriverManager, Statement}
import java.util.UUID
import scala.collection.mutable
import org.joda.time.DateTime

import events.SharedEventData
import log.{Logger, LogLevel}

class SLP(config: Config) {
	private val log: Logger = new Logger(config.getString("log.location"), LogLevel.valueOf(config.getString("log.level")))
	private val serverRoot: String = config.getString("server.root")
	private val serverLog: File = new File(serverRoot + config.getString("server.log"))

	private val dbConnection: Connection = {
		try
		{
			DriverManager.getConnection(config.getString("db.url"), config.getString("db.user"), config.getString("db.password"))
		}
		catch
		{
			case e: SQLException => {
				log.error(e.getErrorCode + "\n\t" + e.getMessage + "\n\t" + e.getStackTrace.map(_.toString).mkString("\n\t\t"))
				throw e
			}
		}
	}

	private def run : Unit = {
		val slw = new ServerLogWatcher(serverLog.getAbsolutePath, false)
		//val slw = new JServerLogWatcher(serverLog, false)
	}
}

object SLP {
	private val slp: SLP = new SLP(ConfigFactory.load())
	private val sharedEventData: mutable.HashMap[Int, SharedEventData] = mutable.HashMap.empty
	private var sessionStartTime: DateTime = null

	def main(args: Array[String]) {
		slp.run
	}

	def getLog: Logger = {
		slp.log
	}

	def getSharedEventData(port: Int): SharedEventData = {
		sharedEventData.get(port).getOrElse(throw new RuntimeException("Server not started yet on port: " + port))
	}

	def initServer(port: Int): SharedEventData = {
		val server: SharedEventData = new SharedEventData(sessionStartTime)
		sharedEventData.put(port, server)
		server
	}

	def startSession(dateTime: DateTime) {
		sharedEventData.clear()
		sessionStartTime = dateTime
	}

	def executeDBStatement(sql: String) {
		val statement: Statement = slp.dbConnection.createStatement()
		statement.execute(sql)
	}

	def insertRawDBStatement(table: String, values: Seq[String]) {
		executeDBStatement(
			"""
			  |INSERT INTO %s
			  |VALUES(%s)
			""".stripMargin.format(table, values.mkString(","))
		)
	}

	def insertDBStatement(table: String, values: Seq[Any]) {
		executeDBStatement(
			"""
			  |INSERT INTO %s
			  |VALUES(%s)
			""".stripMargin.format(table, values.map("'" + _ + "'").mkString(","))
		)
	}

	def updatePlayerName(name: String, vapor: UUID) {
		SLP.executeDBStatement(
			"""
			  |UPDATE players SET name='%1' WHERE vapor_id = '%2';
			  |INSERT INTO players
			  |VALUES('%2', '%1', NULL, NULL) WHERE NOT EXISTS (SELECT 1 FROM players WHERE vapor_id='%2');
			""".stripMargin.format(name, vapor.toString)
		)
	}
}
