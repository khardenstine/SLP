package altitourny.slp.matches

import java.util.UUID

class BallMatch(map: String) extends AbstractMatch(map)
{
	// xp is 50 for goal, 30 for assist, ?? for secondary assist
	def addGoal(source: UUID, assist: Option[UUID], secondaryAssister: Option[UUID])
	{
		getTeam(source).getOrElse(throw new RuntimeException("Non team member scored?")).score + 1

		addOne(goals, source)
		addOne(goalAssists, assist)
		addOne(goalSecondaryAssists, secondaryAssister)

		addValue(exp, source, 50)
		addValue(exp, assist, 30)
		addValue(exp, secondaryAssister, 0)
	}
}