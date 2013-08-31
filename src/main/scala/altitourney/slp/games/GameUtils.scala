package altitourney.slp.games

import altitourney.slp.SLP
import java.sql.SQLException
import java.util.UUID


object GameUtils {
	val goalAssistExp = 30
	val goalSecondaryAssistExp = 0

	def recordTeamScore(gameId: UUID, team: Team, side: Int) = {
		SLP.preparedStatement(
			"""
			  |INSERT INTO game_scores
			  |            (game_id,
			  |             roster_id,
			  |             side,
			  |             score)
			  |VALUES      (?,
			  |             ?,
			  |             ?,
			  |             ?);
			""".stripMargin
		){
			stmt =>
				stmt.setString(1, gameId.toString)
				stmt.setString(2, team.guessRosterId.getOrElse("00000000-0000-0000-0000-00000000000"+side))
				stmt.setInt(3, side)
				stmt.setInt(4, team.getScore)
		}
	}

	def recordPlayer(gameId: UUID, player: UUID, perks: collection.Map[String, PerkData]) {
		perks.foreach{ perk =>
			val perkName = perk._1
			val perkData = perk._2

			try {
				SLP.preparedStatement(
					"""
					  |INSERT INTO games_r
					  |            (game_id,
					  |             vapor_id,
					  |             redperk,
					  |             kills,
					  |             assists,
					  |             deaths,
					  |             exp,
					  |             goals,
					  |             goal_assists,
					  |             secondary_assists,
					  |             timealive)
					  |VALUES     (?,
					  |            ?,
					  |            ?,
					  |            ?,
					  |            ?,
					  |            ?,
					  |            ?,
					  |            ?,
					  |            ?,
					  |            ?);
					""".stripMargin
				){
					stmt =>
						stmt.setString(1, gameId.toString)
						stmt.setString(2, player.toString)
						stmt.setString(3, perkName)
						stmt.setInt(4, perkData.kills)
						stmt.setInt(5, perkData.assists)
						stmt.setInt(6, perkData.deaths)
						stmt.setInt(7, perkData.goals)
						stmt.setInt(8, perkData.goalAssists)
						stmt.setInt(9, perkData.goalSecondaryAssists)
						stmt.setLong(10, perkData.timeAlive.getMillis)
				}
			} catch {
				case e: SQLException => SLP.getLog.error(e)
			}
		}
	}
}
