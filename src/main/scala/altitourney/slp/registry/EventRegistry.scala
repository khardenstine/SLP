package altitourney.slp.registry

import altitourney.slp.SLP
import altitourney.slp.events.EventHandler
import altitourney.slp.events.exceptions.ConsoleCommandException
import play.api.libs.json.{Json, JsValue}

trait EventRegistry {
	type REGISTER = (String, (JsValue) => EventHandler)
	val REGISTRY: Seq[REGISTER]

	protected def getFilter(jsVal: JsValue): String

	def handle(lines: Iterator[String]): Unit = {
		try {
			lines.foreach {
				line =>
					SLP.getRegistryFactory.getEventRegistry.handle(Json.parse(line))
			}
		}
		catch {
			case e: Exception => SLP.getLog.error(e, "Failed to read console command")
		}
	}

	def handle(jsVal: JsValue): Unit = {
		// Ignore bot events
		// this isnt correct
		//val vaporId = (jsVal \ "vaporId").as[String]
		//if (vaporId == JsUndefined || vaporId != "00000000-0000-0000-0000-000000000000")

		val filter = getFilter(jsVal)
		val registers = REGISTRY.filter(_._1 == filter)

		if (registers.length < 1)
		{
			SLP.getLog.info("Event: [" + filter + "] not found in " + this.getClass.getName)
		}
		else
		{
			registers.foreach{ e =>
				SLP.getLog.debug("Handling event: " + e._1)
				workWrapper(() =>
					try {
						e._2(jsVal)
					} catch {
						case e: ConsoleCommandException => {
							try{
								e.propagate(SLP.getServerContext((jsVal \ "port").as[Int]).commandExecutor)
							} catch {
								case e: Exception => SLP.getLog.error(e)
							}
						}
						case e: Exception => SLP.getLog.error(e)
					}
				)
			}
		}
	}

	def workWrapper(work: () => Unit): Unit = {
		work()
	}
}

