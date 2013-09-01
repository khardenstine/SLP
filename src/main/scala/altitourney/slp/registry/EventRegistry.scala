package altitourney.slp.registry

import altitourney.slp.SLP
import altitourney.slp.events.exceptions.ConsoleCommandException
import play.api.libs.json.{Json, JsValue}

trait EventRegistry {
	type REGISTER = (JsValue) => Any
	val EmptyRegister = (jsVal: JsValue) => Unit
	val REGISTRY: Map[String, REGISTER]

	protected def getFilter(jsVal: JsValue): String

	def handleIterator(lines: Iterator[String]): Unit = {
		try {
			lines.foreach{ line =>
				try {
					handle(Json.parse(line))
				} catch {
					case e: Exception => SLP.getLog.error(e, line + "\n" + e.getMessage)
				}
			}
		} catch {
			case e: Exception => SLP.getLog.error(e)
		}
	}

	def handle(jsVal: JsValue): Unit = {
		val filter = getFilter(jsVal)

		REGISTRY.get(filter) match {
			case None => eventNotFound(filter)
			case Some(handler) =>
				SLP.getLog.debug("Handling event: " + filter)
				workWrapper(() =>
					try {
						handler(jsVal)
					} catch {
						case e: ConsoleCommandException => {
							e.propagate(SLP.getServerContext((jsVal \ "port").as[Int]).commandExecutor)
						}
					}
				)
		}
	}

	def eventNotFound(eventName: String) = {
		SLP.getLog.info("Event: [" + eventName + "] not found in " + this.getClass.getName)
	}

	def workWrapper(work: () => Unit): Unit = {
		work()
	}
}

