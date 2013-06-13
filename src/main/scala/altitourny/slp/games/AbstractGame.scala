package altitourny.slp.games

import java.util.UUID
import altitourny.slp.SLP
import org.joda.time.{Duration, DateTime}
import collection.mutable
import altitourny.slp.util.Strings
import java.sql.SQLException

abstract class AbstractGame(final val startTime: DateTime, final val map: String, final val leftTeamId: Int, final val rightTeamId: Int) extends Game {
	SLP.executeDBStatement(
		"""
		  |INSERT INTO maps SELECT '%1$s', '%2$s', (SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE') WHERE NOT EXISTS (SELECT 1 FROM maps WHERE name='%2$s' AND mode_dict=(SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE'));
		""".stripMargin.format(UUID.randomUUID().toString, map, getMode)
	)
	val leftTeam: Team = new Team(leftTeamId)
	val rightTeam: Team = new Team(rightTeamId)
	val kills: mutable.HashMap[UUID, Int] = mutable.HashMap()
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
		addOne(assists, source)
		addValue(exp, source, xp)
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

	def changeTeam(player: UUID, team: Int)
	{
		team match {
			case leftTeam.id =>
			{
				leftTeam.players.add(player)
				if (!rightTeam.players.remove(player))
				{
					initPlayer(player)
				}
			}
			case rightTeam.id =>
			{
				rightTeam.players.add(player)
				if (!leftTeam.players.remove(player))
				{
					initPlayer(player)
				}
			}
			case 2 => {}
			case _ => throw new RuntimeException("No team found for: " + team)
		}
	}

	private def initPlayer(player: UUID)
	{
		kills.put(player, 0)
		assists.put(player, 0)
		deaths.put(player, 0)
		exp.put(player, 0)
		timeAlive.put(player, 0)
		goals.put(player, 0)
		goalAssists.put(player, 0)
		goalSecondaryAssists.put(player, 0)
	}

	private def getResult : String = {
		if (leftTeam.getScore > rightTeam.getScore)
		{
			"LEFT_TEAM"
		}
		else if (leftTeam.getScore < rightTeam.getScore)
		{
			"RIGHT_TEAM"
		}
		else
		{
			"TIE"
		}
	}

	def dump(endTime: DateTime) {
		val gameId = UUID.randomUUID()

		// maps might need escaping

		val values: Seq[String] = Seq(
			Strings.quote(gameId.toString),
			Strings.quote("00000000-0000-0000-0000-000000000000"),
			Strings.quote("00000000-0000-0000-0000-000000000000"),
			Strings.quote("00000000-0000-0000-0000-000000000001"),
			"(SELECT dict_id FROM dicts WHERE dict_type = 'VICTOR' AND dict_value = '%s')".format(getResult),
			Strings.quote(startTime.toString),
			Strings.quote(new Duration(startTime, endTime).getMillis.toString),
			"(SELECT id FROM maps WHERE name = '%s')".format(map),
			Strings.quote(leftTeam.getScore.toString),
			Strings.quote(rightTeam.getScore.toString)
		)
		SLP.insertRawDBStatement("games", values)

		leftTeam.players.foreach(dumpPlayer(gameId, _))
		rightTeam.players.foreach(dumpPlayer(gameId, _))
	}

	def dumpPlayer(gameId: UUID, player: UUID) {
		val values = Seq(
			gameId.toString,
			player.toString,
			kills.get(player).getOrElse(0),
			assists.get(player).getOrElse(0),
			deaths.get(player).getOrElse(0),
			exp.get(player).getOrElse(0),
			goals.get(player).getOrElse(0),
			goalAssists.get(player).getOrElse(0),
			goalSecondaryAssists.get(player).getOrElse(0)
		)

		try
		{
			SLP.insertDBStatement("games_r", values)
		}
		catch
		{
			case e: SQLException => SLP.getLog.error(e)
		}
	}
}
