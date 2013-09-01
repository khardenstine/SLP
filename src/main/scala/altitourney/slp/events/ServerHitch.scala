package altitourney.slp.events

import altitourney.slp.SLP
import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":47674,"duration":569,"changedMap":false,"type":"serverHitch"}
 */
class ServerHitch(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.getLog.warn("Server hitch at %s for %s millis.".format(getTime, getInt("duration")))
}
