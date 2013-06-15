package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

class BallGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId)
{
	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime)
	{
		source foreach { vapor => spawnMap.get(vapor) foreach (_.addGoal(xp))}
		assist foreach { vapor => spawnMap.get(vapor) foreach (_.addGoalAssist(BallGame.goalAssistExp))}
		secondaryAssister foreach { vapor => spawnMap.get(vapor) foreach (_.addSecondaryGoalAssist(BallGame.goalSecondaryAssistExp))}

		spawnMap.values.map(_.end(time))

		getTeam(source.getOrElse(throw new RuntimeException("Who scored?")))
			.getOrElse(throw new RuntimeException("Non team member scored?")).modifyScore(1)
	}
}

object BallGame {
	val goalAssistExp = 30
	val goalSecondaryAssistExp = 0
}