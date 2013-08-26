package altitourney.slp.registry

import altitourney.slp.events.consoleCommands.StartRandom
import play.api.libs.json.JsValue

class LadderRegistry extends EventRegistry{
	val REGISTRY: Seq[REGISTER] = Seq(
		("start_random",					new StartRandom(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "arguments")(0).as[String]
}