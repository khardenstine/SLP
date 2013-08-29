package altitourney.slp.events

import altitourney.slp.SLP
import java.util.UUID
import play.api.libs.json.JsValue
import java.sql.ResultSet

/**
 * {"port":27276,"demo":false,"time":105528343,"level":60,"player":2,"nickname":"{ball}Carlos","aceRank":10,
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientAdd","ip":"192.168.1.2:27272"}
 */
class ClientAdd(jsVal: JsValue) extends EventHandler(jsVal) {
	val vapor: UUID = getUUID("vaporId")
	val nickName: String = getString("nickname")
	getServerContext.addPlayer(vapor, getInt("player"), nickName)
	SLP.updatePlayerName(vapor, nickName)

	private val rulesQuery = """
							   |SELECT players.accepted_rules
							   |FROM players
							   |WHERE players.vapor_id = '%s'
							   |AND players.accepted_rules = FALSE;
							 """.stripMargin.format(vapor.toString)

	SLP.executeDBQuery(rulesQuery, (rs: ResultSet) =>
		if(!rs.getBoolean(1))
			getCommandExecutor.serverWhisper(nickName, "Welcome to Ladder, you must read and accept the rules (type the command '/listRules') before you can play any ladder games.")
	)

	SLP.executeDBStatement(
		"""
		  |INSERT INTO ip_log
		  |VALUES('%s', '%s', '%s');
		""".stripMargin.format(vapor, getString("ip").split(":")(0), getTime)
	)
}
