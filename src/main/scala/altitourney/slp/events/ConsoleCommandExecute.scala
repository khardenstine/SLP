package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":29674784,"arguments":["changeMap","tbd_asteroids"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"vote","group":"Anonymous","type":"consoleCommandExecute"}
 * {"port":27276,"time":29675380,"arguments":["1"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"castBallot","group":"Anonymous","type":"consoleCommandExecute"}
 * {"port":27276,"time":29677385,"arguments":["tbd_asteroids"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"changeMap","group":"Vote","type":"consoleCommandExecute"}
 */
case class ConsoleCommandExecute(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object ConsoleCommandExecute extends Event {
	val logType = "consoleCommandExecute"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new ConsoleCommandExecute(jsVal)
	}
}
