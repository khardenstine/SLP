package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.events.exceptions.InvalidMapSelection
import play.api.libs.json.JsValue

class Start(jsVal: JsValue) extends StartRandom(jsVal) {
	override def getMap: String = {
		val mapArg = (jsVal \ "arguments")(1).as[String]
		if (!getMapList.contains(mapArg))
		{
			throw new InvalidMapSelection(getMapList)
		}
		mapArg
	}
}
