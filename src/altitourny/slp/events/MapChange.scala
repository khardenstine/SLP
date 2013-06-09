package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.matches._

case class MapChange(override val jsVal: JsValue) extends EventHandler(jsVal)
{
	get[String]("mode") match
	{
		case "ball" => getSharedEventData.setMatch(new BallMatch(get("map")))
		case "tbd" => getSharedEventData.setMatch(new TBDMatch(get("map")))
		case _ => getSharedEventData.setMatch(new NoMatch)
	}
}

case object MapChange extends Event
{
	val logType = "mapChange"

	def getEventHandler(jsVal: JsValue)
	{
		MapChange(jsVal)
	}
}
