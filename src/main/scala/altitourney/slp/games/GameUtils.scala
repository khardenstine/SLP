package altitourney.slp.games

import altitourney.slp.SLP
import java.sql.SQLException
import java.util.UUID


object GameUtils {
	def recordTeamScore(gameId: UUID, team: Team, side: Int) = {
		SLP.preparedStatement(
			"""
			  |INSERT INTO game_scores
			  |VALUES (?, ?, ?, ?)
			""".stripMargin
		){
			stmt =>

				stmt.setString(1, gameId.toString)
				stmt.setString(2, team.guessRosterId.getOrElse("00000000-0000-0000-0000-00000000000"+side))
				stmt.setInt(3, side)
				stmt.setInt(4, team.getScore)

				stmt.execute()
		}
	}

	def recordPlayer(gameId: UUID, player: UUID, perks: collection.Map[String, PerkData]) {
		perks.foreach{ perk =>
			try {
				val perkName = perk._1
				val perkData = perk._2

				val values = Seq(
					gameId.toString,
					player.toString,
					perkName,
					perkData.kills,
					perkData.assists,
					perkData.deaths,
					perkData.exp,
					perkData.goals,
					perkData.goalAssists,
					perkData.goalSecondaryAssists,
					perkData.timeAlive.getMillis
				)

				SLP.insertDBStatement("games_r", values)
			}
			catch {
				case e: SQLException => SLP.getLog.error(e)
			}
		}
	}
}
