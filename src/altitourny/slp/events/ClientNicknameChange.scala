package altitourny.slp.events

import play.api.libs.json.JsValue
import java.util.UUID

case class ClientNicknameChange(override val jsVal: JsValue) extends EventHandler(jsVal)
{
	getSharedEventData.updatePlayerName(UUID.fromString(get("vaporId")), get("newNickName"))
}

case object ClientNicknameChange extends Event
{
	val logType = "clientNicknameChange"

	def getEventHandler(jsVal: JsValue)
	{
		new ClientNicknameChange(jsVal)
	}
}
