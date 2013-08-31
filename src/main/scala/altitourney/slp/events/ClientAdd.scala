package altitourney.slp.events

import altitourney.slp.SLP
import java.sql.Timestamp
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.{Success, Failure}

/**
 * {"port":27276,"demo":false,"time":105528343,"level":60,"player":2,"nickname":"{ball}Carlos","aceRank":10,
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientAdd","ip":"192.168.1.2:27272"}
 */
class ClientAdd(jsVal: JsValue) extends EventHandler(jsVal) {
	val vapor: UUID = getUUID("vaporId")
	val nickName: String = getString("nickname")
	getServerContext.addPlayer(vapor, getInt("player"), nickName)
	SLP.updatePlayerName(vapor, nickName)

	val welcomeNewPlayers = "Welcome to Ladder, you must read and accept the rules (type the command '/listRules') before you can play any ladder games."

	SLP.preparedQuery(
		"""
		  |SELECT players.accepted_rules
		  |FROM players
		  |WHERE players.vapor_id = ?
		  |AND players.accepted_rules = FALSE
		  |LIMIT 1;
		""".stripMargin,
		stmt => stmt.setString(1, vapor.toString),
		rs => rs.getBoolean(1)
	) match {
		case Failure(e) => SLP.getLog.error(e)
		case Success(rs) =>
		    if(!rs.head)
				getCommandExecutor.serverWhisper(nickName, welcomeNewPlayers)
	}

	SLP.preparedStatement(
		"""
		  |INSERT INTO ip_log
		  |VALUES(?, ?, ?);
		""".stripMargin
	){
		stmt =>
			stmt.setString(1, vapor.toString)
			stmt.setString(2, getString("ip").split(":")(0))
			stmt.setTimestamp(3, new Timestamp(getTime.getMillis))
	}
}
