package altitourney.slp.registry

import altitourney.slp.events.{MapChange, ServerInit, SessionStart}
import play.api.libs.json.JsValue

protected class StartUpRegistry extends EventRegistry {
	val REGISTRY: Seq[REGISTER] = Seq(
		("serverInit",				new ServerInit(_)),
		("sessionStart",			new SessionStart(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "type").as[String]
}
