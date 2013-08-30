package altitourney.slp.events.consoleCommands

import altitourney.slp.SLP
import altitourney.slp.events.EventHandler
import altitourney.slp.events.exceptions.{LadderNotConfigured, ServerWhisperException}
import play.api.libs.json.JsValue
import scala.util.{Failure, Success}

class LadderTop10(jsVal: JsValue) extends EventHandler(jsVal) {
	val whisperTo = getServerContext.getPlayerName(getUUID("source"))
	val mode = getServerContext.getLadderMode.getOrElse(throw new LadderNotConfigured())

	val query = """
				  |SELECT players.name, players.%1$s_rating
				  |FROM players
				  |ORDER BY players.%1$s_rating DESC
				  |LIMIT 10;
				""".stripMargin.format(mode)

	SLP.executeDBQuery(
		query,
		rs => (rs.getString("name"), rs.getInt("%s_rating".format(mode)))
	) match {
		case Success(results) =>
			getCommandExecutor.serverWhisper(whisperTo, s"Top 10 in $mode:")
			results.zipWithIndex.foreach{ zipped =>
				getCommandExecutor.serverWhisper(whisperTo, "%s) %s - %s".format(zipped._2 + 1, zipped._1._2, zipped._1._1))
				if (zipped._2 == 4)
					Thread.sleep(1500)
			}
		case Failure(e) =>
			SLP.getLog.error(e)
			throw new ServerWhisperException(whisperTo, "Error reading ladder Top 10.")
	}
}
