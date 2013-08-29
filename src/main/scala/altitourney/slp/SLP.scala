package altitourney.slp

import altitourney.slp.commands.CommandExecutorFactory
import altitourney.slp.registry.RegistryFactory
import com.typesafe.config.{ConfigFactory, Config}
import java.io.File
import java.net.HttpURLConnection
import java.sql.{Connection, PreparedStatement, ResultSet, SQLException, Statement, Timestamp}
import java.util.UUID
import log.{Logger, LogLevel}
import org.joda.time.DateTime
import scala.collection.mutable
import scala.util.Try

private class SLP(config: Config) {
	private val log: Logger = new Logger(config.getString("log.location"), LogLevel.valueOf(config.getString("log.level")))
	private val serverRoot: String = config.getString("server.root")
	private val serverLog: File = new File(serverRoot + config.getString("server.log"))
	log.info(serverLog.getCanonicalPath)
	private var running = false

	private val CommandExecutorFactory = new CommandExecutorFactory(serverRoot + config.getString("server.command"))

	private def buildServerContext(port: Int, startTime: DateTime, name: String): ServerContext = {
		new ServerContext(config.getConfig(port.toString), port, startTime, name)
	}

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
						case e: Exception => log.error(e, "Process failed.")
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
							log.error(e, "Process failed.")
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
				log.error(e, e.getErrorCode + "\n\t" + e.getMessage)
				throw e
			}
		}
	}

	Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
		def run() {
			log.info("Shutting down")
			try {
				val ip = SLP.getIP

				SLP.serverContexts foreach {
					tuple: ((Int, ServerContext)) =>
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
	}))

	def getConnection: Connection = {
		connectionPoolManager.getConnection
	}

	def releaseConnection(conn: Connection) {
		connectionPoolManager.release(conn)
	}

	def shutdown() {
		running = false
	}
}

object SLP {
	private val slp: SLP = new SLP(ConfigFactory.load())
	private val serverContexts: mutable.HashMap[Int, ServerContext] = mutable.HashMap.empty
	// TODO null wtf?
	private var sessionStartTime: DateTime = null

	def main(args: Array[String]) {
		slp.start()
	}

	def getLog: Logger = {
		slp.log
	}

	def getServerContext(port: Int): ServerContext = {
		serverContexts.get(port).getOrElse(sys.error("Server not initialized on port: " + port))
	}

	def initServer(port: Int, name: String): ServerContext = {
		val server: ServerContext = slp.buildServerContext(port, sessionStartTime, name)
		serverContexts.put(port, server)
		server
	}

	def startSession(dateTime: DateTime) {
		serverContexts.clear()
		sessionStartTime = dateTime
	}

	lazy val getRegistryFactory = new RegistryFactory

	lazy val getCommandExecutorFactory = slp.CommandExecutorFactory

	def getIP: String = {
		val url = new java.net.URL("http://api.exip.org/?call=ip")

		url.openConnection() match {
			case conn: HttpURLConnection => {
				val ipStr = scala.io.Source.fromInputStream(conn.getInputStream).getLines().mkString("")
				conn.disconnect()
				ipStr
			}
			case _ => sys.error("HttpURLConnection not acquired")
		}
	}

	def preparedStatement(sql: String)(fn: (PreparedStatement) => Unit) {
		val connection = slp.getConnection
		try {
			fn(connection.prepareStatement(sql))
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

	def executeDBQuery[T](sql: String, fn: ResultSet => T): Try[Seq[T]] = Try {
		val connection = slp.getConnection
		try {
			val statement: Statement = connection.createStatement()
			getLog.debug("Executing query: " + sql)
			try {
				val resultSet = statement.executeQuery(sql)
				try {
					new Iterator[ResultSet] {
						override def hasNext = resultSet.next()
						override def next() = resultSet
					}.map(fn).toList
				} finally {
					resultSet.close()
				}
			} finally {
	          	statement.close()
			}
		} finally {
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
		insertRawDBStatement(table, values.map("'" + _ + "'"))
	}

	def updatePlayerName(vapor: UUID, name: String) {
		preparedStatement(
			// IM BROKEN
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

			serverContexts foreach {
				tuple: ((Int, ServerContext)) =>
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
