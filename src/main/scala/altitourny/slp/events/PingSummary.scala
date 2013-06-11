package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":8838,"type":"pingSummary","pingByPlayer":{"5":0}}
 */
case class PingSummary(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object PingSummary extends Event {
	val logType = "pingSummary"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new PingSummary(jsVal)
	}
}
