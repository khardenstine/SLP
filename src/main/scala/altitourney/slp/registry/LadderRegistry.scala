package altitourney.slp.registry

import play.api.libs.json.JsValue
import altitourney.slp.events.consoleCommands.StartRandom

class LadderRegistry extends EventRegistry{
	val REGISTRY: Seq[REGISTER] = Seq(
		("start_random",					new StartRandom(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "arguments")(0).as[String]
}