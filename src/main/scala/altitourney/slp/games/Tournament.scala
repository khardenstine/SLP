package altitourney.slp.games

import org.joda.time.DateTime

abstract class Tournament(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId)  {
	def dump(endTime: DateTime): Unit = {
		record(endTime)
	}
}

