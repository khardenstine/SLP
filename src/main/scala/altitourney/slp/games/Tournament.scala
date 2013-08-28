package altitourney.slp.games

import org.joda.time.DateTime

trait Tournament extends AbstractGame {
	def dump(endTime: DateTime): Unit = {
		record(endTime)
	}
}

