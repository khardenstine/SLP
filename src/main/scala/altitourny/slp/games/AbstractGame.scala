package altitourny.slp.games

import java.util.UUID
import altitourny.slp.SLP
import org.joda.time.{Duration, DateTime}
import scala.collection.{JavaConversions, JavaConverters, concurrent}
import java.sql.{Timestamp, SQLException}
import com.google.common.collect.HashBasedTable
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractGame(final val startTime: DateTime, final val map: String, final val leftTeamId: Int, final val rightTeamId: Int) extends Game {
	private val leftTeam: Team = new Team(leftTeamId)
	private val rightTeam: Team = new Team(rightTeamId)
	// todo column shouldnt be string, should be Perk Enum
	private val perkTable: HashBasedTable[UUID, String, PerkData] = HashBasedTable.create()
	val spawnMap: concurrent.Map[UUID, PlayerSpawn] = JavaConversions.mapAsScalaConcurrentMap(new ConcurrentHashMap[UUID, PlayerSpawn])

	def addKill(source: Option[UUID], victim: UUID, xp: Int, time: DateTime) {
		source foreach{vapor: UUID => spawnMap.get(vapor) foreach (_.addKill(xp))}
		spawnMap.get(victim) foreach (_.addDeath(time))
	}

	def addAssist(source: UUID, xp: Int) {
		spawnMap.get(source) foreach (_.addAssist(xp))
	}

	def spawn(player: UUID, perk: String, time: DateTime) {
		spawnMap.get(player) match {
			case Some(ps: PlayerSpawn) => {
				if (ps.redPerk.equalsIgnoreCase(perk))
				{
					ps.respawn(time)
				}
				else
				{
					perkTable.put(player, ps.redPerk, ps.getPerkData + Option(perkTable.get(player, ps.redPerk)))
					spawnMap.put(player, new PlayerSpawn(perk, time))
				}
			}
			case None => spawnMap.put(player, new PlayerSpawn(perk, time))
		}
	}

	def getTeam(player: UUID): Option[Team] = {
		if (leftTeam.players.contains(player)) {
			Some(leftTeam)
		}
		else if (rightTeam.players.contains(player)) {
			Some(rightTeam)
		}
		else {
			None
		}
	}

	def changeTeam(player: UUID, team: Int) {
		team match {
			case leftTeam.id => {
				leftTeam.players.add(player)
				rightTeam.players.remove(player)
			}
			case rightTeam.id => {
				rightTeam.players.add(player)
				leftTeam.players.remove(player)
			}
			case 2 => {}
			case _ => throw new RuntimeException("No team found for: " + team)
		}
	}

	private def getResult: String = {
		if (leftTeam.getScore > rightTeam.getScore) {
			"LEFT_TEAM"
		}
		else if (leftTeam.getScore < rightTeam.getScore) {
			"RIGHT_TEAM"
		}
		else {
			"TIE"
		}
	}

	def dumpSpawnMap(endTime: DateTime) : HashBasedTable[UUID, String, PerkData] = {
		spawnMap.map{case (player: UUID, ps: PlayerSpawn) =>
			ps.end(endTime)	// all player lives should have ended already, this is just in case they have not
			perkTable.put(player, ps.redPerk, ps.getPerkData + Option(perkTable.get(player, ps.redPerk)))
		}
		perkTable
	}

	def dump(endTime: DateTime) {
		dumpSpawnMap(endTime)

		val gameId = UUID.randomUUID()

		val stmt = SLP.prepareStatement(
			"""
			  |INSERT INTO games
			  |VALUES (?, ?, ?, ?, (SELECT dict_id FROM dicts WHERE dict_type = 'VICTOR' AND dict_value = ?), ?, ?, (SELECT id FROM maps WHERE name = ?), ?, ?)
			""".stripMargin
		)

		stmt.setString(1, gameId.toString)
		stmt.setString(2, "00000000-0000-0000-0000-000000000000")
		stmt.setString(3, "00000000-0000-0000-0000-000000000000")
		stmt.setString(4, "00000000-0000-0000-0000-000000000001")
		stmt.setString(5, getResult)
		stmt.setTimestamp(6, new Timestamp(startTime.getMillis))
		stmt.setFloat(7, new Duration(startTime, endTime).getMillis)
		stmt.setString(8, map)
		stmt.setInt(9, leftTeam.getScore)
		stmt.setInt(10, rightTeam.getScore)

		stmt.execute()

		leftTeam.players.foreach(dumpPlayer(gameId, _))
		rightTeam.players.foreach(dumpPlayer(gameId, _))
	}

	def dumpPlayer(gameId: UUID, player: UUID) {
		JavaConverters.asScalaSetConverter(perkTable.row(player).entrySet()).asScala.foreach{ entry =>
			try {
				dumpPerk(gameId, player, entry.getKey, entry.getValue)
			}
			catch {
				case e: SQLException => SLP.getLog.error(e)
			}
		}
	}

	def dumpPerk(gameId: UUID, player: UUID, perk: String, perkData: PerkData) {
		val values = Seq(
			gameId.toString,
			player.toString,
			perk,
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
}
