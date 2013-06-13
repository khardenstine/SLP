package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":18780,"streak":1,"source":"plane","player":3,"victim":1,"multi":1,"xp":10,"type":"kill"}
 */
case class Kill(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.getGame().addKill(getUUIDfromPlayerNumber("player"), getUUIDfromPlayerNumber("victim").get, getInt("xp"))
}

case object Kill extends Event {
	val logType = "kill"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new Kill(jsVal)
	}
}
