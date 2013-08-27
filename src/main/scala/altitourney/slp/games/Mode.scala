package altitourney.slp.games

import org.joda.time.DateTime

sealed trait Mode {
	val mode: String

	def teamSize: Int
	def buildGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game
	override def toString = mode
}

object Mode {
	def withName(str: String): Option[Mode] = {
		str match {
			case TBD.mode => Some(TBD)
			case BALL.mode => Some(BALL)
			case _ => None
		}
	}
}

case object TBD extends Mode {
	val mode = "tbd"
	def teamSize: Int = 5
	def buildGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new TBDGame(startTime, map, leftTeamId, rightTeamId)
	}
}

case object BALL extends Mode {
	val mode = "ball"
	def teamSize: Int = 6
	def buildGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		new BallGame(startTime, map, leftTeamId, rightTeamId)
	}
}
