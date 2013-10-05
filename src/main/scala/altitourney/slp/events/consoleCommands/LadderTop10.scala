package altitourney.slp.events.consoleCommands

import altitourney.slp.SLP
import altitourney.slp.events.EventHandler
import altitourney.slp.events.exceptions.ServerWhisperException
import play.api.libs.json.JsValue
import scala.util.{Failure, Success}

class LadderTop10(jsVal: JsValue) extends EventHandler(jsVal) {
	val whisperTo = getServerContext.getPlayerName(getUUID("source"))
	val mode = getServerContext.getLadderMode

	val query = """
				  |SELECT ladder_ranks.name,
				  |       ladder_ranks.%1$s_rating,
				  |       ladder_ranks.%1$s_rank
				  |FROM   ladder_ranks
				  |ORDER  BY ladder_ranks.%1$s_rank ASC
				  |LIMIT 10;
				""".stripMargin.format(mode)

	SLP.preparedQuery(query){
		rs => ModeRating(
			rs.getString("name"),
			rs.getInt("%s_rank".format(mode)),
			rs.getInt("%s_rating".format(mode))
		)
	} match {
		case Success(results) =>
			getCommandExecutor.serverWhisper(whisperTo, s"Top 10 in $mode:")
			results.zipWithIndex.foreach{ zipped =>
				getCommandExecutor.serverWhisper(whisperTo, "%s) %s - %s".format(zipped._1.rank, zipped._1.rating, zipped._1.name))
				if (zipped._2 == 4)
					Thread.sleep(1500)
			}
		case Failure(e) =>
			SLP.getLog.error(e)
			throw new ServerWhisperException(whisperTo, "Error reading ladder Top 10.")
	}
}

case class ModeRating(
						 name: String,
						 rank: Int,
						 rating: Int
						 )
