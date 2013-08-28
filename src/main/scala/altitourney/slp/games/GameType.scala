package altitourney.slp.games

import org.joda.time.DateTime

trait Standard extends AbstractGame {
	def dump(endTime: DateTime): Unit = {}
}

trait Ladder extends AbstractGame {
	def dump(endTime: DateTime): Unit = {
		record(endTime)
	}
}

trait Tournament extends AbstractGame {
	def dump(endTime: DateTime): Unit = {
		record(endTime)
	}
}

