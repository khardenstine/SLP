package altitourney.slp.registry

import play.api.libs.json.JsValue
import altitourney.slp.events.EventHandler
import altitourney.slp.events.exceptions.ServerMessageException
import altitourney.slp.SLP

trait EventRegistry {
	type REGISTER = (String, (JsValue) => EventHandler)
	val REGISTRY: Seq[REGISTER]

	protected def getFilter(jsVal: JsValue): String

	def handle(jsVal: JsValue): Unit = {
		// Ignore bot events
		// this isnt correct
		//val vaporId = (jsVal \ "vaporId").as[String]
		//if (vaporId == JsUndefined || vaporId != "00000000-0000-0000-0000-000000000000")
		REGISTRY
			.filter(_._1 == getFilter(jsVal))
			.foreach{ e =>
				SLP.getLog.debug("Handling event: " + e._1)
				workWrapper(() =>
					try {
						e._2(jsVal)
					} catch {
						case e: ServerMessageException => {
							try{
								SLP.getSharedEventData((jsVal \ "port").as[Int]).commandExecutor.serverMessage(e)
							} catch {
								case e: Exception => SLP.getLog.error(e)
							}
						}
						case e: Exception => SLP.getLog.error(e)
					}
				)
			}
	}

	def workWrapper(work: () => Unit): Unit = {
		work()
	}
}

