package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port" : 27276.0, "time" : 3786.0, "type" : "serverStart"}
 */
class ServerStart(jsVal: JsValue) extends EventHandler(jsVal) {
	getServerContext.clearPlayers()
}
