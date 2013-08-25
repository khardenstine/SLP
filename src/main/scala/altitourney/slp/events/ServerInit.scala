package altitourney.slp.events

import play.api.libs.json.JsValue
import altitourney.slp.SLP

/**
 * {"port":27276,"time":73005,"name":"Altitude Server","type":"serverInit","maxPlayerCount":14}
 */
class ServerInit(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.initServer(port, getString("name"))

	SLP.getLog.info(getString("name") + " started on port " + port + " at " + getTime)
}
