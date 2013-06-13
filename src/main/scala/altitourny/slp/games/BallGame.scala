package altitourny.slp.games

import java.util.UUID
import org.joda.time.DateTime

class BallGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId)
{
	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int) {
		getTeam(source.getOrElse(throw new RuntimeException("Who scored?")))
			.getOrElse(throw new RuntimeException("Non team member scored?")).modifyScore(1)

		addOne(goals, source)
		addOne(goalAssists, assist)
		addOne(goalSecondaryAssists, secondaryAssister)

		addValue(exp, source, xp)
		addValue(exp, assist, BallGame.goalAssistExp)
		addValue(exp, secondaryAssister, BallGame.goalSecondaryAssistExp)
	}

	def getMode = "ball_6v6"
}

object BallGame
{
	val goalAssistExp = 30
	val goalSecondaryAssistExp = 0
}