package altitourney.slp.registry

import altitourney.slp.events.{SessionStart, ServerStart}
import play.api.libs.json.JsValue

protected class StartUpRegistry extends EventRegistry {
	val REGISTRY: Seq[REGISTER] = Seq(
		("serverStart",				new ServerStart(_)),
		("sessionStart",			new SessionStart(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "type").as[String]
}
