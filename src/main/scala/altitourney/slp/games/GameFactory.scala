package altitourney.slp.games

import altitourney.slp.SLP
import java.util.UUID
import org.joda.time.DateTime

trait GameFactory {
	def build(mode: Mode, startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
	def buildNoGame(map: String = " ", leftTeamId: Int = 0, rightTeamId: Int = 1): Game = {
		SLP.getLog.debug("Building no game")
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
			SLP.getLog.debug("Factory already built a game")
			buildNoGame(map, leftTeamId, rightTeamId)
		} else {
			hasBuilt = true
			mode match {
				case TBD =>
					SLP.getLog.debug("Building %s TBD game".format(facType))
					buildTBD(startTime, map, leftTeamId, rightTeamId)
				case BALL =>
					SLP.getLog.debug("Building %s BALL game".format(facType))
					buildBall(startTime, map, leftTeamId, rightTeamId)
				case _ =>
					SLP.getLog.debug("Building %s no game".format(facType))
					buildNoGame(map, leftTeamId, rightTeamId)
			}
		}
	}

	protected val facType: String

	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
}

class LadderFactory(ratings: Map[UUID, Int]) extends AbstractGameFactory {
	protected val facType = "Ladder"
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Ladder(ratings, startTime, map, leftTeamId, rightTeamId) with TBDGame
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Ladder(ratings, startTime, map, leftTeamId, rightTeamId) with BallGame
	}
}

class TournamentFactory extends AbstractGameFactory {
	protected val facType = "Tournament"
	protected def buildTBD(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Tournament(startTime, map, leftTeamId, rightTeamId) with TBDGame
	}
	protected def buildBall(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new Tournament(startTime, map, leftTeamId, rightTeamId) with BallGame
	}
}
