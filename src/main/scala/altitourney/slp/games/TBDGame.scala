package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

trait TBDGame extends AbstractGame {
	val mode = TBD
	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime) {}
}