package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.games._

/**
 * {"port":27276,"leftTeam":3,"time":3898,"rightTeam":4,"map":"tbd_lostcity","type":"mapChange","mode":"tbd"}
 */
case class MapChange(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	getString("mode") match {
		case "ball" => getSharedEventData.setGame(new BallGame(getTime, getString("map"), getInt("leftTeam"), getInt("rightTeam")))
		case "tbd" => getSharedEventData.setGame(new NoGame)//new TBDGame(getTime, getString("map"), getInt("leftTeam"), getInt("rightTeam")))
		case _ => getSharedEventData.setGame(new NoGame)
	}
}

case object MapChange extends Event {
	val logType = "mapChange"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		MapChange(jsVal)
	}
}
