package altitourney.slp.games

import altitourney.slp.{ServerContext, SLP}
import com.google.common.collect.HashBasedTable
import java.sql.Timestamp
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.joda.time.{Duration, DateTime}
import scala.collection.{JavaConversions, concurrent}

abstract class AbstractGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends Game(map, leftTeamId, rightTeamId) {
	val mode: Mode
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

	private def getResult: String = {
		if (leftTeam.getScore > rightTeam.getScore) {
			leftTeam.guessRosterId.getOrElse("00000000-0000-0000-0000-000000000000")
		}
		else if (leftTeam.getScore < rightTeam.getScore) {
			rightTeam.guessRosterId.getOrElse("00000000-0000-0000-0000-000000000001")
		}
		else {
			"00000000-0000-0000-0000-000000000000"
			//this is wronggggggggggggggggggggggggggggg
			//should be null
		}
	}

	def end(endTime: DateTime, serverContext: ServerContext): Unit = {
		spawnMap.map{case (player: UUID, ps: PlayerSpawn) =>
			ps.end(endTime)	// all player lives should have ended already, this is just in case they have not
			perkTable.put(player, ps.redPerk, ps.getPerkData + Option(perkTable.get(player, ps.redPerk)))
		}

		dump(endTime, serverContext)
	}

	protected def dump(endTime: DateTime, serverContext: ServerContext): Unit

	def record(endTime: DateTime) {
		val gameId = UUID.randomUUID()

		SLP.preparedStatement(
			"""
			  |INSERT INTO games
			  |VALUES (?, ?, ?, ?, ?, (SELECT id FROM maps WHERE name = ?))
			""".stripMargin
		){
			stmt =>
				stmt.setString(1, gameId.toString)
				stmt.setString(2, "00000000-0000-0000-0000-000000000000")
				stmt.setString(3, getResult)
				stmt.setTimestamp(4, new Timestamp(startTime.getMillis))
				stmt.setFloat(5, new Duration(startTime, endTime).getMillis)
				stmt.setString(6, map)
		}

		GameUtils.recordTeamScore(gameId, leftTeam, 0)
		GameUtils.recordTeamScore(gameId, rightTeam, 1)

		JavaConversions.mapAsScalaMap(perkTable.rowMap()).foreach{ player =>
			GameUtils.recordPlayer(gameId, player._1, JavaConversions.mapAsScalaMap(player._2))
		}
	}
}
