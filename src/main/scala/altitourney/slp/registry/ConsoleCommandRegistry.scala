package altitourney.slp.registry

import altitourney.slp.events.consoleCommands._
import altitourney.slp.{SLP, ThreadHelper}
import play.api.libs.json.JsValue

protected class ConsoleCommandRegistry extends EventRegistry{
	val REGISTRY: Map[String, REGISTER] = Map(
		("acceptRules",				new AcceptRules(_)),
		("assignTeam",				EmptyRegister),
		("castBallot",				EmptyRegister),
		("changeMap",				EmptyRegister),
		("kick",					EmptyRegister),
		("listBans",				EmptyRegister),
		("listMaps",				EmptyRegister),
		("listPlayers",				EmptyRegister),
		("logPlanePositions",		EmptyRegister),
		("removeBan",				EmptyRegister),
		("ladder",					SLP.getRegistryFactory.getLadderRegistry.handle(_)),
		("ladderTop10",				new LadderTop10(_)),
		("listRules",				new ListRules(_)),
		("logServerStatus",			EmptyRegister),
		("ratings",					new Ratings(_)),
		("serverMessage",			EmptyRegister),
		("serverWhisper",			EmptyRegister),
		("startTournament",			EmptyRegister),
		("stopTournament",			EmptyRegister),
		("vote",					EmptyRegister)
	)

	def getFilter(jsVal: JsValue): String = (jsVal \ "command").as[String]

	override def workWrapper(work: () => Unit): Unit = {
		ThreadHelper.startThread(new Runnable {
			def run() {
				try {
					work()
				} catch {
					case e: Exception => SLP.getLog.error(e)
				}
			}
		})
	}
}
