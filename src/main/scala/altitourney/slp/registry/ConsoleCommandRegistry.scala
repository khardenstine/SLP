package altitourney.slp.registry

import altitourney.slp.events.consoleCommands._
import play.api.libs.json.JsValue

protected class ConsoleCommandRegistry extends EventRegistry{
	val REGISTRY: Seq[REGISTER] = Seq(
		("ladder",					new Ladder(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "command").as[String]
}
