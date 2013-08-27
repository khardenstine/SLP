package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":81310,"player":5,"victim":0,"xp":4,"type":"assist"}
 */
class Assist(jsVal: JsValue) extends EventHandler(jsVal) {
	getServerContext.getGame.addAssist(getUUIDfromPlayerNumber("player").get, getInt("xp"))
}
