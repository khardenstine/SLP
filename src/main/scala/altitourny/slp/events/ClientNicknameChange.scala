package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.SLP

case class ClientNicknameChange(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.updatePlayerName(getUUIDfromJSON("vaporId").get, getString("newNickName"))
	SLP.updatePlayerName(getString("newNickName"), getUUIDfromJSON("vaporId").get)
}

case object ClientNicknameChange extends Event {
	val logType = "clientNicknameChange"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new ClientNicknameChange(jsVal)
	}
}
