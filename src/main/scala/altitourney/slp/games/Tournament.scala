package altitourney.slp.games

import altitourney.slp.ServerContext
import java.util.UUID
import org.joda.time.DateTime

abstract class Tournament(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId)  {
	def dump(gameId: UUID, endTime: DateTime, serverContext: ServerContext): Unit = {
		record(gameId, endTime)
		serverContext.commandExecutor.stopTournament()
	}
}

