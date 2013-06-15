package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":32799,"player":1,"secondaryAssister":-1,"xp":50,"type":"goal","assister":-1}
 */
case class Goal(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.getGame.addGoal(getUUIDfromPlayerNumber("player"), getUUIDfromPlayerNumber("assister"), getUUIDfromPlayerNumber("secondaryAssister"), getInt("xp"), getTime)
}

case object Goal extends Event {
	val logType = "goal"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new Goal(jsVal)
	}
}

