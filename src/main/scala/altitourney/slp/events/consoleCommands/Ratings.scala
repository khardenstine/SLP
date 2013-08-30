package altitourney.slp.events.consoleCommands

import altitourney.slp.events.EventHandler
import play.api.libs.json.JsValue
import altitourney.slp.SLP
import altitourney.slp.events.exceptions.ServerWhisperException
import scala.util.{Failure, Success}

class Ratings(jsVal: JsValue) extends EventHandler(jsVal) {
	val whisperTo = getServerContext.getPlayerName(getUUID("source"))

	val playerName = (jsVal \ "arguments")(0).as[String]
	val vapor = getServerContext.getPlayerUUID(playerName).getOrElse(throw new ServerWhisperException(whisperTo, "Error reading %s's stats.".format(playerName)))
	val query = """
				  |SELECT players.tbd_rating, players.ball_rating
				  |FROM players
				  |WHERE players.vapor_id = ?;
				""".stripMargin

	val ratingsList = SLP.executeDBQuery(
		query,
		(p) => p.setString(1, vapor.toString),
		(rs) => (rs.getInt("tbd_rating"), rs.getInt("ball_rating"))
	)

	ratingsList match {
		case Success(rating) => rating.foreach{ r =>
			getCommandExecutor.serverWhisper(whisperTo, "%s :: TBD :: Rating:%s".format(playerName, r._1))
			getCommandExecutor.serverWhisper(whisperTo, "%s :: BALL :: Rating:%s".format(playerName, r._2))
		}
		case Failure(e) =>
			SLP.getLog.error(e)
			throw new ServerWhisperException(whisperTo, "Error reading %s's stats.".format(playerName))
	}
}
