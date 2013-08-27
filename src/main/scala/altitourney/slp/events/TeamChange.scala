package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27277,"time":4167,"player":0,"team":2,"type":"teamChange"}
 */
class TeamChange(jsVal: JsValue) extends EventHandler(jsVal) {
	getGame.changeTeam(getUUIDfromPlayerNumber("player").get, getInt("team"), getServerContext)
}
