package altitourny.slp.matches

import java.util.UUID

class NoMatch extends Match
{
	def addKill(source: Option[UUID], victim: UUID, xp: Int)
	{}

	def addGoal(source: UUID, assist: Option[UUID], secondaryAsssiter: Option[UUID])
	{}

	def addAssist(source: UUID, xp: Int)
	{}

	def dump()
	{}
}
