package altitourney.slp

import java.sql.{Connection, DriverManager}
import scala.collection.mutable

protected class ConnectionPoolManager(private val poolSize: Int, private val databaseUrl: String, private val userName: String, private val password: String)
{
	private val pool: mutable.Queue[Connection] = new mutable.Queue()
	for(i <- 0 to poolSize)
	{
		pool.enqueue(DriverManager.getConnection(databaseUrl, userName, password))
	}

	def getConnection : Connection =
	{
		var opCon : Option[Connection] = getConn
		while (opCon.isEmpty)
		{
			Thread.sleep(100)
			opCon = getConn
		}
		opCon.get
	}

	private def getConn : Option[Connection] = synchronized
	{
		if (pool.size > 0)
		{
			val conn = pool.dequeue()
			if (conn.isClosed)
			{
				Some(DriverManager.getConnection(databaseUrl, userName, password))
			}
			else
			{
				Some(conn)
			}
		}
		else
		{
			None
		}
	}

	def release(conn: Connection) {
		synchronized {
			pool.enqueue(conn)
		}
	}

	def closeAll()
	{
		while (pool.size > 0)
		{
			pool.dequeue().close()
		}
	}
}