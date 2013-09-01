package altitourney.slp.registry

import altitourney.slp.events.{ServerInit, SessionStart}
import play.api.libs.json.JsValue

protected class StartUpRegistry extends EventRegistry {
	val REGISTRY: Map[String, REGISTER] = Map(
		("serverInit",				new ServerInit(_)),
		("sessionStart",			new SessionStart(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "type").as[String]

	override def eventNotFound(eventName: String) = Unit
}
