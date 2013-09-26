package altitourney.slp.events.consoleCommands.ladder

import play.api.libs.json.JsValue
import scala.util.Random

class StartRandom(jsVal: JsValue) extends LadderStart(jsVal) {
	def getMap: String = {
		Random.shuffle(getMapList).head
	}
}
