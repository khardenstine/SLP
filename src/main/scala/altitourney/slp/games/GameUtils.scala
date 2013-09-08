package altitourney.slp.games

import altitourney.slp.events.consoleCommands.ladder.LadderUtils
import altitourney.slp.{Util, SLP}
import java.sql.{Timestamp, SQLException}
import java.util.UUID
import org.joda.time.{Duration, DateTime}

object GameUtils {
	val goalAssistExp = 30
	val goalSecondaryAssistExp = 0

	def recordGameMetaData(gameId: UUID, startTime: DateTime, endTime: DateTime, map: String, victorId: String): Unit = {
		SLP.preparedStatement(
			"""
			  |INSERT INTO games
			  |            (game_id,
			  |             match_id,
			  |             victor,
			  |             start_time,
			  |             duration,
			  |             map)
			  |VALUES      (?,
			  |             ?,
			  |             ?,
			  |             ?,
			  |             ?,
			  |             (SELECT id
			  |              FROM   maps
			  |              WHERE  name = ?));
			""".stripMargin
		) {
			stmt =>
				stmt.setString(1, gameId.toString)
				stmt.setString(2, "00000000-0000-0000-0000-000000000000")
				stmt.setString(3, victorId)
				stmt.setTimestamp(4, new Timestamp(startTime.getMillis))
				stmt.setFloat(5, new Duration(startTime, endTime).getMillis)
				stmt.setString(6, map)
		}
	}

	def recordTeamScore(gameId: UUID, team: Team, side: Int, avgRatings: Option[RatingsChange]): Unit = {
		SLP.preparedStatement(
			"""
			  |INSERT INTO game_scores
			  |            (game_id,
			  |             roster_id,
			  |             side,
			  |             score,
			  |             old_avg_rating,
			  |             new_avg_rating)
			  |VALUES      (?,
			  |             ?,
			  |             ?,
			  |             ?,
			  |             ?,
			  |             ?);
			""".stripMargin
		){
			implicit stmt =>
				stmt.setString(1, gameId.toString)
				stmt.setString(2, team.guessRosterId.getOrElse("00000000-0000-0000-0000-00000000000"+side))
				stmt.setInt(3, side)
				stmt.setInt(4, team.getScore)
				Util.setOptionalInt(5, avgRatings.map(_.oldRating))
				Util.setOptionalInt(6, avgRatings.map(_.newRating))
		}
	}

	def recordPlayer(gameId: UUID, player: UUID, perks: collection.Map[String, PerkData]): Unit = {
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
					  |             base_damage,
					  |             other_damage,
					  |             base_destroys,
					  |             other_destroys,
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
						stmt.setInt(7, perkData.exp)
						stmt.setInt(8, perkData.goals)
						stmt.setInt(9, perkData.goalAssists)
						stmt.setInt(10, perkData.goalSecondaryAssists)
						stmt.setInt(11, perkData.baseDamage)
						stmt.setInt(12, perkData.otherDamage)
						stmt.setInt(13, perkData.baseDestroys)
						stmt.setInt(14, perkData.otherDestroys)
						stmt.setLong(15, perkData.timeAlive.getMillis)
				}
			} catch {
				case e: SQLException => SLP.getLog.error(e)
			}
		}
	}

	def updatePlayerRating(player: UUID, ratingChange: Int, gameMode: Mode): Unit = {
		try {
			SLP.preparedStatement(
				"""
				  |UPDATE ratings
				  |SET    %1$s_rating = %1$s_rating + (?)
				  |WHERE  vapor_id = ?;
				  |
				  |INSERT INTO ratings
				  |            (vapor_id,
				  |             season_id,
				  |             %1$s_rating,
				  |             accepted_rules)
				  |SELECT ?,
				  |       Current_ladder_season(),
				  |       ?,
				  |       false
				  |WHERE  NOT EXISTS (SELECT 1
				  |                   FROM   ladder_ranks
				  |                   WHERE  vapor_id = ?);
				""".stripMargin.format(gameMode)
			){
				stmt =>
					stmt.setInt(1, ratingChange)
					stmt.setString(2, player.toString)
					stmt.setString(3, player.toString)
					stmt.setInt(4, ratingChange + LadderUtils.ladderStartingRating)
					stmt.setString(5, player.toString)
			}
		} catch {
			case e: Exception => SLP.getLog.error(e)
		}
	}

	def insertPlayerRatingsChange(gameId: UUID, ratingChange: PlayerRatingsChange): Unit = {
		try {
			SLP.preparedStatement(
				"""
				  |INSERT INTO games_ratings
				  |            (vapor_id,
				  |             game_id,
				  |             old_rating,
				  |             new_rating)
				  |VALUES      (?,
				  |             ?,
				  |             ?,
				  |             ?);
				""".stripMargin
			){
				stmt =>
					stmt.setString(1, ratingChange.player.toString)
					stmt.setString(2, gameId.toString)
					stmt.setInt(3, ratingChange.oldRating)
					stmt.setInt(4, ratingChange.newRating)
			}
		} catch {
			case e: Exception => SLP.getLog.error(e)
		}
	}
}
