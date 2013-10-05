package altitourney.slp.events

import altitourney.slp.SLP
import altitourney.slp.events.ClientAdd._
import java.sql.Timestamp
import java.util.UUID
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import scala.util.{Try, Success, Failure}

/**
 * {"port":27276,"demo":false,"time":105528343,"level":60,"player":2,"nickname":"{ball}Carlos","aceRank":10,
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientAdd","ip":"192.168.1.2:27272"}
 */
class ClientAdd(jsVal: JsValue) extends EventHandler(jsVal) {
	val vapor: UUID = getUUID("vaporId")
	val nickName: String = getString("nickname")
	getServerContext.addPlayer(vapor, getInt("player"), nickName)
	SLP.updatePlayerName(vapor, nickName)

	if (getServerContext.ladderMode.isSuccess) {
		hasAcceptedRules(vapor) match {
			case Failure(e) => SLP.getLog.error(e)
			case Success(b) if b => getCommandExecutor.serverWhisper(nickName, WELCOME_NEW_PLAYERS)
			case _ =>
		}
	}

	insertIPLog(vapor, getString("ip").split(":")(0), getTime)
}

object ClientAdd {

	val WELCOME_NEW_PLAYERS = "Welcome to Ladder, you must read and accept the rules (type the command '/listRules') before you can play any ladder games."

	def hasAcceptedRules(vapor: UUID): Try[Boolean] = {
			SLP.preparedQuery(
			"""
			  |SELECT Has_accepted_rules(?);
			""".stripMargin,
			_.setString(1, vapor.toString),
			_.getBoolean(1)
		).map(!_.head)
	}

	def insertIPLog(vapor: UUID, IP_Address: String, date: DateTime): Unit = {
		SLP.preparedStatement(
			"""
			  |INSERT INTO ip_log
			  |            (vapor_id,
			  |             ip_address,
			  |             insert_date)
			  |VALUES     (?, ?, ?);
			""".stripMargin
		){
			stmt =>
				stmt.setString(1, vapor.toString)
				stmt.setString(2, IP_Address)
				stmt.setTimestamp(3, new Timestamp(date.getMillis))
		}
	}
}
