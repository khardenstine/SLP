package altitourney.slp.events

import play.api.libs.json.JsValue
import altitourney.slp.SLP

/**
 * {"port":27276,"time":29674784,"arguments":["changeMap","tbd_asteroids"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"vote","group":"Anonymous","type":"consoleCommandExecute"}
 * {"port":27276,"time":29675380,"arguments":["1"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"castBallot","group":"Anonymous","type":"consoleCommandExecute"}
 * {"port":27276,"time":29677385,"arguments":["tbd_asteroids"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"changeMap","group":"Vote","type":"consoleCommandExecute"}
 */
class ConsoleCommandExecute(jsVal: JsValue) extends EventHandler(jsVal) {
	try {
		SLP.getLog.debug("Handling custom command event: " + jsVal \ "type")
		//ConsoleCommandExecute.ALL//.map(_.handle(jsVal))
	}
	catch {
		case e: Exception => SLP.getLog.error(e)
	}

}

case object ConsoleCommandExecute {

/*	val ALL: Seq = Seq(

	)   */
}
