package altitourney.slp.events

import altitourney.slp.SLP
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsValue

/**
 * {"port":-1,"time":0,"date":"2013 Feb 08 23:56:35:711 EST","type":"sessionStart"}
 */
class SessionStart(jsVal: JsValue) extends EventHandler(jsVal) {
	val dateTimeFormat = "yyyy MMM dd HH:mm:ss:SSS"

	val dt = DateTimeFormat.forPattern(dateTimeFormat).parseDateTime(getString("date").substring(0, dateTimeFormat.length))
	SLP.startSession(dt)
}
