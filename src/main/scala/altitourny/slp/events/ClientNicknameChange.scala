package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.SLP
import java.util.UUID

/**
 * {"port":27276,"oldNickname":"'LOL';--UMADBRO","time":83170918,"player":5,"newNickname":"LOL';--UMADBRO",
 * "vaporId":"351b3a83-71d0-490e-8e71-ddf67fc2091c","type":"clientNicknameChange","ip":"192.168.1.2:27272"}
 */
case class ClientNicknameChange(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	val newNick: String = getString("newNickName")
	val vapor: UUID = getUUID("vaporId")

	getSharedEventData.updatePlayerName(vapor, newNick)
	SLP.updatePlayerName(vapor, newNick)
}

case object ClientNicknameChange extends Event {
	val logType = "clientNicknameChange"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new ClientNicknameChange(jsVal)
	}
}
