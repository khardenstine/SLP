package altitourney.slp.registry

import altitourney.slp.events.consoleCommands.ladder._
import play.api.libs.json.JsValue

class LadderRegistry extends EventRegistry{
	val REGISTRY: Seq[REGISTER] = Seq(
		("start_random",	new StartRandom(_)),
		("stop",			new Stop(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "arguments")(0).as[String]
}