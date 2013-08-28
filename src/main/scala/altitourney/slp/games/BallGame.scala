package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

trait BallGame extends AbstractGame {
	val mode = BALL

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime)
	{
		source foreach { vapor => spawnMap.get(vapor) foreach (_.addGoal(xp))}
		assist foreach { vapor => spawnMap.get(vapor) foreach (_.addGoalAssist(GameUtils.goalAssistExp))}
		secondaryAssister foreach { vapor => spawnMap.get(vapor) foreach (_.addSecondaryGoalAssist(GameUtils.goalSecondaryAssistExp))}

		spawnMap.values.map(_.end(time))

		getTeam(source.getOrElse(sys.error("Who scored?")))
			.getOrElse(sys.error("Non team member scored?")).modifyScore(1)
	}
}
