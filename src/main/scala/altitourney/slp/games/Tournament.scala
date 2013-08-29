package altitourney.slp.games

import org.joda.time.DateTime
import altitourney.slp.ServerContext

abstract class Tournament(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId)  {
	def dump(endTime: DateTime, serverContext: ServerContext): Unit = {
		serverContext.commandExecutor.stopTournament()
		record(endTime)
	}
}

