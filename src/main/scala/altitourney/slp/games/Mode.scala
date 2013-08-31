package altitourney.slp.games

import altitourney.slp.SLP

sealed trait Mode {
	val name: String
	def teamSize: Int
	override def toString = name
}

object Mode {
	def withName(str: String): Mode = {
		str match {
			case TBD.name => TBD
			case BALL.name => BALL
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
	val name = "tbd"
	def teamSize: Int = 5
}

case object BALL extends Mode {
	val name = "ball"
	def teamSize: Int = 6
}
