package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port" : 27276.0, "time" : 3786.0, "type" : "serverStart"}
 */
case class ServerStart(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.clearPlayers()
}

case object ServerStart extends Event {
	val logType = "serverStart"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new ServerStart(jsVal)
	}
}
