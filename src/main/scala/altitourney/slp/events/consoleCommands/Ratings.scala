package altitourney.slp.events.consoleCommands

import altitourney.slp.SLP
import altitourney.slp.events.EventHandler
import altitourney.slp.events.exceptions.ServerWhisperException
import play.api.libs.json.JsValue
import scala.util.{Failure, Success}

class Ratings(jsVal: JsValue) extends EventHandler(jsVal) {
	val whisperTo = getServerContext.getPlayerName(getUUID("source"))

	val playerName = (jsVal \ "arguments")(0).as[String]
	val vapor = getServerContext.getPlayerUUID(playerName).getOrElse(
		throw new ServerWhisperException(whisperTo, "Error reading %s's stats.".format(playerName))
	)

	SLP.preparedQuery(
		"""
		  |SELECT ladder_ranks.tbd_rating, ladder_ranks.tbd_rank, ladder_ranks.ball_rating, ladder_ranks.ball_rank
		  |FROM ladder_ranks
		  |WHERE players.vapor_id = ?;
		""".stripMargin,
		_.setString(1, vapor.toString),
		rs => Rating(rs.getInt("tbd_rating"), rs.getInt("tbd_rank"), rs.getInt("ball_rating"), rs.getInt("ball_rank"))
	) match {
		case Success(rating) => rating.foreach{ r =>
			getCommandExecutor.serverWhisper(whisperTo, "%s : TBD - Rank %s, Rating %s".format(playerName, r.tbdRank, r.tbdRating))
			getCommandExecutor.serverWhisper(whisperTo, "%s : BALL - Rank %s, Rating %s".format(playerName, r.ballRank, r.ballRating))
		}
		case Failure(e) =>
			SLP.getLog.error(e)
			throw new ServerWhisperException(whisperTo, "Error reading %s's stats.".format(playerName))
	}
}

case class Rating(
					 tbdRating: Int,
					 tbdRank: Int,
					 ballRating: Int,
					 ballRank: Int
					 )
