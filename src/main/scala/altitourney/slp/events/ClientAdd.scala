package altitourney.slp.events

import play.api.libs.json.JsValue
import java.util.UUID
import altitourney.slp.SLP

/**
 * {"port":27276,"demo":false,"time":105528343,"level":60,"player":2,"nickname":"{ball}Carlos","aceRank":10,
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientAdd","ip":"192.168.1.2:27272"}
 */
case class ClientAdd(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
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

case object ClientAdd extends Event {
	val logType = "clientAdd"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new ClientAdd(jsVal)
	}
}