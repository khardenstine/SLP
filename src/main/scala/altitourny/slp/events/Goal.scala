package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":32799,"player":1,"secondaryAssister":-1,"xp":50,"type":"goal","assister":-1}
 * @param jsVal
 */
case class Goal(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.getMatch().addGoal(getUUIDfromJSON("player").get, getUUIDfromJSON("assister"), getUUIDfromJSON("secondaryAssister"))
}

case object Goal extends Event {
	val logType = "goal"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new Goal(jsVal)
	}
}

