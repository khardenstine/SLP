package altitourny.slp.events

import play.api.libs.json.JsValue

case class TeamChange(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object TeamChange extends Event
{
	val logType = "teamChange"

	def getEventHandler(jsVal: JsValue)
	{
		new TeamChange(jsVal)
	}
}
