package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.SLP
import org.joda.time.format.DateTimeFormat

/**
 * {"port":-1,"time":0,"date":"2013 Feb 08 23:56:35:711 EST","type":"sessionStart"}
 */
case class SessionStart(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	val dateTimeFormat = "yyyy MMM dd HH:mm:ss:SSS"

	val dt = DateTimeFormat.forPattern(dateTimeFormat).parseDateTime(getString("date").substring(0, dateTimeFormat.length))
	SLP.startSession(dt)
}

case object SessionStart extends Event {
	val logType = "sessionStart"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new SessionStart(jsVal)
	}
}
