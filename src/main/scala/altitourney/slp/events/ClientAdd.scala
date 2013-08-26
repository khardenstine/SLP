package altitourney.slp.events

import altitourney.slp.SLP
import java.util.UUID
import play.api.libs.json.JsValue

/**
 * {"port":27276,"demo":false,"time":105528343,"level":60,"player":2,"nickname":"{ball}Carlos","aceRank":10,
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientAdd","ip":"192.168.1.2:27272"}
 */
class ClientAdd(jsVal: JsValue) extends EventHandler(jsVal) {
	val vapor: UUID = getUUID("vaporId")
	val nickName: String = getString("nickname")
	getSharedEventData.addPlayer(vapor, getInt("player"), nickName)
	SLP.updatePlayerName(vapor, nickName)

	SLP.executeDBStatement(
		"""
		  |INSERT INTO ip_log
		  |VALUES('%s', '%s', '%s')
		""".stripMargin.format(vapor, getString("ip").split(":")(0), getTime)
	)
}
