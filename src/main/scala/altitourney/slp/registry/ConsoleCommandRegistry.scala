package altitourney.slp.registry

import altitourney.slp.ThreadHelper
import altitourney.slp.events.consoleCommands._
import play.api.libs.json.JsValue

protected class ConsoleCommandRegistry extends EventRegistry{
	val REGISTRY: Seq[REGISTER] = Seq(
		("acceptRules",				new AcceptRules(_)),
		("ladder",					new Ladder(_)),
		("ladderTop10",				new LadderTop10(_)),
		("ratings",					new Ratings(_)),
		("listRules",				new ListRules(_))
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "command").as[String]

	override def workWrapper(work: () => Unit): Unit = {
		ThreadHelper.startThread(new Runnable {
			def run() {
				work()
			}
		})
	}
}
