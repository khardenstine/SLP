package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":81310,"player":5,"victim":0,"xp":4,"type":"assist"}
 */
case class Assist(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.getGame().addAssist(getUUIDfromPlayerNumber("player").get, getInt("xp"))
}

case object Assist extends Event {
	val logType = "assist"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		Assist(jsVal)
	}
}
