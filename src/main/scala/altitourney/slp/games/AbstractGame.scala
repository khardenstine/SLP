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
	var result: Option[Result] = None

	protected implicit def uuid2Op(uuid: UUID): Option[UUID] = Some(uuid)

	def playerSpawnAction(source: Option[UUID], fn: PlayerSpawn => Unit) {
		source.foreach(vapor => spawnMap.get(vapor).foreach(fn))
	}

	def addKill(source: Option[UUID], victim: UUID, xp: Int, time: DateTime) {
		playerSpawnAction(source, _.addKill(xp))
		playerSpawnAction(victim, _.addDeath(time))
	}

	def addAssist(source: UUID, xp: Int) {
		playerSpawnAction(source, _.addAssist(xp))
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

	def setResult(result: Result): Unit = {
		this.result match {
			case None => this.result = Some(result)
			case Some(x) => sys.error("Result already has a value.")
		}
	}

	protected def getVictor: Option[Team] = {
		result.get match {
			case Decisive =>
				if (leftTeam.getScore > rightTeam.getScore) {
					Some(leftTeam)
				} else {
					Some(rightTeam)
				}
			case Tie => None
		}
	}

	private def getVictorRosterId: String = {
		getVictor match {
			case Some(team) =>
				team.guessRosterId.getOrElse("00000000-0000-0000-0000-000000000000")
			case None =>
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
		// We will only have a result if the a tournamentRoundEnd event was fired.
		// That is also the only time we want to be dumping the game data
		result.foreach(_ => dump(UUID.randomUUID(), endTime, serverContext))
	}

	protected def dump(gameId: UUID, endTime: DateTime, serverContext: ServerContext): Unit

	def record(gameId: UUID, endTime: DateTime, teamAvgRatings: Option[(RatingsChange, RatingsChange)] = None) {
		GameUtils.recordGameMetaData(gameId, startTime, endTime, map, getVictorRosterId)

		GameUtils.recordTeamScore(gameId, leftTeam, 0, teamAvgRatings.map(_._1))
		GameUtils.recordTeamScore(gameId, rightTeam, 1, teamAvgRatings.map(_._2))

		JavaConversions.mapAsScalaMap(perkTable.rowMap()).foreach{ player =>
			GameUtils.recordPlayer(gameId, player._1, JavaConversions.mapAsScalaMap(player._2))
		}
	}
}
