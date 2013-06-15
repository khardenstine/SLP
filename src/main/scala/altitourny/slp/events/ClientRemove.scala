package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"message":"left","time":105531995,"player":2,"reason":"Client left.","nickname":"{ball}Carlos",
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientRemove","ip":"192.168.1.2:27272"}
 */
case class ClientRemove(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.removePlayer(getInt("player"))
}

case object ClientRemove extends Event {
	val logType = "clientRemove"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new ClientRemove(jsVal)
	}
}