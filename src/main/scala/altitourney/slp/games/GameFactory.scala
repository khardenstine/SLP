package altitourney.slp.games

import org.joda.time.DateTime

trait GameFactory {
	def build(mode: Mode, startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		mode match {
			case TBD => buildTBD(startTime, map, leftTeamId, rightTeamId)
			case BALL => buildBall(startTime, map, leftTeamId, rightTeamId)
			case _ => buildNoGame(startTime, map, leftTeamId, rightTeamId)
		}
	}

	def buildNoGame(startTime: DateTime = new DateTime(), map: String = " ", leftTeamId: Int = 0, rightTeamId: Int = 1): Game = {
		new NoGame(startTime, map, leftTeamId, rightTeamId)
	}

	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
}

object StandardFactory extends GameFactory {
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new TBDGame(startTime, map, leftTeamId, rightTeamId) with Standard
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new BallGame(startTime, map, leftTeamId, rightTeamId) with Standard
	}
}

object LadderFactory extends GameFactory {
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new TBDGame(startTime, map, leftTeamId, rightTeamId) with Ladder
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new BallGame(startTime, map, leftTeamId, rightTeamId) with Ladder
	}
}

object TournamentFactory extends GameFactory {
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new TBDGame(startTime, map, leftTeamId, rightTeamId) with Tournament
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new BallGame(startTime, map, leftTeamId, rightTeamId) with Tournament
	}
}
