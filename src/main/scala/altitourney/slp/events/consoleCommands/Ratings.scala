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
		throw new ServerWhisperException(whisperTo, s"Error reading $playerName's stats.")
	)

	SLP.preparedQuery(
		"""
		  |SELECT ladder_ranks.tbd_rating,
		  |       ladder_ranks.tbd_rank,
		  |       ladder_ranks.ball_rating,
		  |       ladder_ranks.ball_rank
		  |FROM   ladder_ranks
		  |WHERE  ladder_ranks.vapor_id = ?;
		""".stripMargin,
		_.setString(1, vapor.toString),
		rs => Rating(rs.getInt("tbd_rating"), rs.getInt("tbd_rank"), rs.getInt("ball_rating"), rs.getInt("ball_rank"))
	) match {
		case Success(rating) => {
			if (rating.isEmpty) {
				getCommandExecutor.serverWhisper(whisperTo, s"$playerName has no ratings.")
			} else {
				rating.foreach{ r =>
					getCommandExecutor.serverWhisper(whisperTo, "%s : TBD - Rank %s, Rating %s".format(playerName, r.tbdRank, r.tbdRating))
					getCommandExecutor.serverWhisper(whisperTo, "%s : BALL - Rank %s, Rating %s".format(playerName, r.ballRank, r.ballRating))
				}
			}
		}
		case Failure(e) =>
			SLP.getLog.error(e)
			throw new ServerWhisperException(whisperTo, s"Error reading $playerName's stats.")
	}
}

case class Rating(
					 tbdRating: Int,
					 tbdRank: Int,
					 ballRating: Int,
					 ballRank: Int
					 )
