package altitourny.slp.events

import play.api.libs.json.JsValue

case class TeamChange(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object TeamChange extends Event {
	val logType = "teamChange"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new TeamChange(jsVal)
	}
}
