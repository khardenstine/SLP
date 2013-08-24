package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":32799,"player":1,"secondaryAssister":-1,"xp":50,"type":"goal","assister":-1}
 */
class Goal(jsVal: JsValue) extends EventHandler(jsVal) {
	getSharedEventData.getGame.addGoal(getUUIDfromPlayerNumber("player"), getUUIDfromPlayerNumber("assister"), getUUIDfromPlayerNumber("secondaryAssister"), getInt("xp"), getTime)
}
