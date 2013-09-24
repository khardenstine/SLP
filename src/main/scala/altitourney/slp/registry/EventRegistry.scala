package altitourney.slp.registry

import altitourney.slp.events.exceptions.ConsoleCommandException
import altitourney.slp.{ThreadHelper, SLP}
import play.api.libs.json.{Json, JsValue}
import scala.collection.mutable

trait EventRegistry {
	type REGISTER = (JsValue) => Any
	val EmptyRegister = (jsVal: JsValue) => Unit
	val REGISTRY: Map[String, REGISTER]

	protected def getEventName(jsVal: JsValue): String

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
		val eventName = getEventName(jsVal)

		REGISTRY.get(eventName) match {
			case None => eventNotFound(eventName)
			case Some(handler) =>
				SLP.getLog.debug("Handling event: %s at %s".format(eventName, (jsVal \ "time").as[String]))
				val port = (jsVal \ "port").as[Int]
				workWrapper(() =>
					try {
						handler(jsVal)
					} catch {
						case e: ConsoleCommandException => {
							e.propagate(SLP.getServerContext(port).commandExecutor)
						}
					}
				)
				portedEventListener.synchronized(
					portedEventListener.remove((port, eventName))
				).foreach(_.foreach(ThreadHelper.startThread))
		}
	}

	def eventNotFound(eventName: String) = {
		SLP.getLog.info("Event: [" + eventName + "] not found in " + this.getClass.getName)
	}

	def workWrapper(work: () => Unit): Unit = {
		work()
	}

	private val portedEventListener = new mutable.HashMap[(Int, String), mutable.Set[Runnable]] with mutable.MultiMap[(Int, String), Runnable]

	def addPortedEventListener(eventListener: (Int, String, () => Unit)): Unit = {
		addPortedEventListener(eventListener._1, eventListener._2, eventListener._3)
	}

	def addPortedEventListener(port: Int, eventName: String, fun: () => Unit): Unit = {
		REGISTRY.get(eventName).getOrElse(sys.error("No event registered with name " + eventName))
		portedEventListener.synchronized(
			portedEventListener.addBinding((port, eventName), new Runnable {
				def run() = {
					try {
						fun()
					} catch {
						case e: Exception => SLP.getLog.error(e)
					}
				}
			})
		)
	}
}

