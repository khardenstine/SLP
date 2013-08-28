package altitourney.slp.games

import altitourney.slp.SLP

sealed trait Mode {
	val mode: String
	def teamSize: Int
	override def toString = mode
}

object Mode {
	def withName(str: String): Mode = {
		str match {
			case TBD.mode => TBD
			case BALL.mode => BALL
			case _ => {
				// log it and throw it
				val message = "Cannot parse game mode: " + str
				SLP.getLog.error(message)
				sys.error(message)
			}
		}
	}
}

case object TBD extends Mode {
	val mode = "tbd"
	def teamSize: Int = 5
}

case object BALL extends Mode {
	val mode = "ball"
	def teamSize: Int = 6
}
