package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.matches._

case class MapChange(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getString("mode") match {
		case "ball" => getSharedEventData.setMatch(new BallMatch(getTime, getString("map")))
		case "tbd" => getSharedEventData.setMatch(new TBDMatch(getTime, getString("map")))
		case _ => getSharedEventData.setMatch(new NoMatch)
	}
}

case object MapChange extends Event {
	val logType = "mapChange"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		MapChange(jsVal)
	}
}
