package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

trait GameFactory {
	def build(mode: Mode, startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
	def buildNoGame(map: String = " ", leftTeamId: Int = 0, rightTeamId: Int = 1): Game = {
		new NoGame(map, leftTeamId, rightTeamId)
	}
}

object StandardFactory extends GameFactory {
	def build(mode: Mode, startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		buildNoGame(map, leftTeamId, rightTeamId)
	}
}

trait AbstractGameFactory extends GameFactory{
	// we want a factory instance to only ever build one game
	private var hasBuilt = false

	def build(mode: Mode, startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		if (hasBuilt) {
			buildNoGame(map, leftTeamId, rightTeamId)
		} else {
			hasBuilt = true
			mode match {
				case TBD => buildTBD(startTime, map, leftTeamId, rightTeamId)
				case BALL => buildBall(startTime, map, leftTeamId, rightTeamId)
				case _ => buildNoGame(map, leftTeamId, rightTeamId)
			}
		}
	}

	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
}

class LadderFactory(ratings: Map[UUID, Int]) extends AbstractGameFactory {
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Ladder(ratings, startTime, map, leftTeamId, rightTeamId) with TBDGame
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Ladder(ratings, startTime, map, leftTeamId, rightTeamId) with BallGame
	}
}

object TournamentFactory extends AbstractGameFactory {
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Tournament(startTime, map, leftTeamId, rightTeamId) with TBDGame
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Tournament(startTime, map, leftTeamId, rightTeamId) with BallGame
	}
}
