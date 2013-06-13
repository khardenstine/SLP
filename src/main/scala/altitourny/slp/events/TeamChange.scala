package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27277,"time":4167,"player":0,"team":2,"type":"teamChange"}
 */
case class TeamChange(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getSharedEventData.getGame().changeTeam(getUUIDfromPlayerNumber("player").get, getInt("team"))
}

case object TeamChange extends Event {
	val logType = "teamChange"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new TeamChange(jsVal)
	}
}
