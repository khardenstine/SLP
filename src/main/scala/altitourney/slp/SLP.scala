package altitourney.slp

import com.typesafe.config.{ConfigFactory, Config}
import java.io.File
import java.sql._
import java.util.UUID
import scala.collection.mutable
import org.joda.time.DateTime

import altitourney.slp.events.SharedEventData
import log.{Logger, LogLevel}
import scala.Array
import java.net.HttpURLConnection

class SLP(config: Config) {
	private val log: Logger = new Logger(config.getString("log.location"), LogLevel.valueOf(config.getString("log.level")))
	private val serverRoot: String = config.getString("server.root")
	private val serverLog: File = new File(serverRoot + config.getString("server.log"))
	log.info(serverLog.getCanonicalPath)
	private var running = false

	private def start() = {
		running = true
		val slw = new ServerLogWatcher(serverLog.getAbsolutePath)

		ThreadHelper.startThread(new Runnable {
			def run() {
				while (running) {
					try {
						slw.checkServerLogForNewData()
						ThreadHelper.sleep(200)
					}
					catch {
						case e: Exception => log.error("Process failed " + e)
					}
				}
			}
		})

		// Callback thread
		ThreadHelper.startDaemonThread(new Runnable {
			def run() {
				while (running) {
					try {
						SLP.callback()
						ThreadHelper.sleep(600000)
					}
					catch {
						case e: Exception => {
							log.error("Process failed " + e)
						}
					}
				}
			}
		})
	}

	private val connectionPoolManager = {
		try {
			new ConnectionPoolManager(2, config.getString("db.url"), config.getString("db.user"), config.getString("db.password"))
		}
		catch {
			case e: SQLException => {
				log.error(e.getErrorCode + "\n\t" + e.getMessage + "\n\t" + e.getStackTrace.map(_.toString).mkString("\n\t\t"))
				throw e
			}
		}
	}

	def getConnection: Connection = {
		connectionPoolManager.getConnection
	}

	def releaseConnection(conn: Connection) {
		connectionPoolManager.release(conn)
	}

	def shutdown() {
		running = false

		try {
			val ip = SLP.getIP

			SLP.sharedEventData foreach {
				tuple: ((Int, SharedEventData)) =>
					try {
						SLP.preparedStatement(
							"""
							  |DELETE FROM servers WHERE ip = ? AND port = ?;
							""".stripMargin
						) {
							stmt =>
								stmt.setString(1, ip)
								stmt.setString(2, tuple._1.toString)

								stmt.execute()
						}
					}
					catch {
						case e: SQLException => log.error(e)
					}
			}
		}
		catch {
			case e: Exception => log.error(e)
		}

		connectionPoolManager.closeAll()
	}
}

object SLP {
	private val slp: SLP = new SLP(ConfigFactory.load())
	private val sharedEventData: mutable.HashMap[Int, SharedEventData] = mutable.HashMap.empty
	private var sessionStartTime: DateTime = null

	def main(args: Array[String]) {
		slp.start()
	}

	def getLog: Logger = {
		slp.log
	}

	def getSharedEventData(port: Int): SharedEventData = {
		sharedEventData.get(port).getOrElse(throw new RuntimeException("Server not started yet on port: " + port))
	}

	def initServer(port: Int, name: String): SharedEventData = {
		val server: SharedEventData = new SharedEventData(sessionStartTime, name)
		sharedEventData.put(port, server)
		server
	}

	def startSession(dateTime: DateTime) {
		sharedEventData.clear()
		sessionStartTime = dateTime
	}

	def getIP: String = {
		val url = new java.net.URL("http://api.exip.org/?call=ip")

		url.openConnection() match {
			case conn: HttpURLConnection => {
				val ipStr = scala.io.Source.fromInputStream(conn.getInputStream).getLines().mkString("")
				conn.disconnect()
				ipStr
			}
			case _ => throw new RuntimeException("HttpURLConnection not acquired")
		}
	}

	def preparedStatement(sql: String)(fn: (PreparedStatement) => Unit) {
		val connection = slp.getConnection
		try {
			fn.apply(connection.prepareStatement(sql))
		}
		finally {
			slp.releaseConnection(connection)
		}
	}

	def executeDBStatement(sql: String) {
		val connection = slp.getConnection
		try {
			val statement: Statement = connection.createStatement()
			getLog.debug("Executing query: " + sql)
			statement.execute(sql)
		}
		finally {
			slp.releaseConnection(connection)
		}
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

	def updatePlayerName(vapor: UUID, name: String) {
		preparedStatement(
			"""
			  |UPDATE players SET name= ? WHERE vapor_id = ?;
			  |INSERT INTO players
			  |SELECT ?, ?, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM players WHERE vapor_id = ?);
			""".stripMargin
		) {
			stmt =>
				stmt.setString(1, name)
				stmt.setString(2, vapor.toString)
				stmt.setString(3, vapor.toString)
				stmt.setString(4, name)
				stmt.setString(5, vapor.toString)

				stmt.execute()
		}
	}

	def callback() {
		try {
			val ip = getIP
			getLog.debug("Server IP: " + ip)

			sharedEventData foreach {
				tuple: ((Int, SharedEventData)) =>
					try {
						preparedStatement(
							"""
							  |UPDATE servers SET callback = ?, name = ? WHERE ip = ? AND port = ?;
							  |INSERT INTO servers
							  |SELECT ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM servers WHERE ip = ? AND port = ?);
							""".stripMargin
						) {
							stmt =>

								val dt = new DateTime()
								stmt.setTimestamp(1, new Timestamp(dt.getMillis))
								stmt.setString(2, tuple._2.name)
								stmt.setString(3, ip)
								stmt.setString(4, tuple._1.toString)
								stmt.setString(5, tuple._2.name)
								stmt.setString(6, ip)
								stmt.setString(7, tuple._1.toString)
								stmt.setTimestamp(8, new Timestamp(dt.getMillis))
								stmt.setString(9, ip)
								stmt.setString(10, tuple._1.toString)

								stmt.execute()
						}
					}
					catch {
						case e: SQLException => getLog.error(e)
					}
			}
		}
		catch {
			case e: Exception => getLog.error(e)
		}
	}
}
