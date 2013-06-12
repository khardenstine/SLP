package altitourny.slp.matches

import java.util.UUID
import altitourny.slp.SLP
import org.joda.time.{Duration, DateTime}
import collection.mutable
import collection.mutable.HashMap
import altitourny.slp.util.Strings

abstract class AbstractMatch(final val startTime: DateTime, final val map: String) extends Match {
	SLP.executeDBStatement(
		"""
		  |INSERT INTO maps SELECT '%1$s', '%2$s', (SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MATCH_MODE') WHERE NOT EXISTS (SELECT 1 FROM maps WHERE name='%2$s' AND mode_dict=(SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MATCH_MODE'));
		""".stripMargin.format(UUID.randomUUID().toString, map, getMode)
	)
	val teamA: Team = new Team()
	val teamB: Team = new Team()
	val kills: HashMap[UUID, Int] = mutable.HashMap()
	val assists: mutable.HashMap[UUID, Int] = mutable.HashMap()
	val deaths: mutable.HashMap[UUID, Int] = mutable.HashMap()
	val exp: mutable.HashMap[UUID, Int] = mutable.HashMap()
	val timeAlive: mutable.HashMap[UUID, Int] = mutable.HashMap()
	val goals: mutable.HashMap[UUID, Int] = mutable.HashMap()
	val goalAssists: mutable.HashMap[UUID, Int] = mutable.HashMap()
	val goalSecondaryAssists: mutable.HashMap[UUID, Int] = mutable.HashMap()

	def getMode : String

	def addOne[A](mapObj: mutable.Map[A, Int], key: A) {
		addValue(mapObj, key, 1)
	}

	def addValue[A](mapObj: mutable.Map[A, Int], key: A, value: Int) {
		mapObj.put(key, mapObj.get(key).getOrElse(0) + value)
	}

	def addOne[A](mapObj: mutable.Map[A, Int], key: Option[A]) {
		addValue(mapObj, key, 1)
	}

	def addValue[A](mapObj: mutable.Map[A, Int], key: Option[A], value: Int) {
		if (key.isDefined) {
			addValue(mapObj, key.get, value)
		}
	}

	def addKill(source: Option[UUID], victim: UUID, xp: Int) {
		addOne(kills, source)
		addValue(exp, source, xp)
		addOne(deaths, victim)
	}

	def addAssist(source: UUID, xp: Int) {
		addValue(assists, source, xp)
	}

	def getTeam(player: UUID): Option[Team] = {
		if (teamA.players.contains(player)) {
			Some(teamA)
		}
		else if (teamB.players.contains(player)) {
			Some(teamB)
		}
		else {
			None
		}
	}

	def dump(endTime: DateTime) {
		val matchId = UUID.randomUUID()

		val values: Seq[String] = Seq(
			Strings.quote(matchId.toString),
			Strings.quote("00000000-0000-0000-0000-000000000000"),
			Strings.quote("00000000-0000-0000-0000-000000000000"),
			Strings.quote("00000000-0000-0000-0000-000000000000"),
			"(SELECT dict_id FROM dicts WHERE dict_type = 'MATCH_VICTOR' AND dict_value = '%s')".format("TEAM A"),
			Strings.quote(startTime.toString),
			Strings.quote(new Duration(startTime, endTime).getMillis.toString),
			"(SELECT id FROM maps WHERE name = '%s')".format(map),
			Strings.quote(teamA.score.toString),
			Strings.quote(teamB.score.toString)
		)
		SLP.insertRawDBStatement("matches", values)

		teamA.players.foreach(dumpPlayer(matchId, _))
		teamB.players.foreach(dumpPlayer(matchId, _))
	}

	def dumpPlayer(matchId: UUID, player: UUID) {
		val values = Seq(
			matchId,
			player,
			kills.get(player),
			assists.get(player),
			deaths.get(player),
			exp.get(player),
			goals.get(player),
			goalAssists.get(player),
			goalSecondaryAssists.get(player)
		)

		SLP.insertDBStatement("matches_r", values)
	}
}
