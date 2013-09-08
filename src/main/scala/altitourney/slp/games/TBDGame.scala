package altitourney.slp.games

import altitourney.slp.structures.{Base, Target}
import java.util.UUID
import org.joda.time.DateTime

trait TBDGame extends AbstractGame {
	val mode = TBD

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime) {}

	def structureDamage(source: Option[UUID], target: Option[Target], xp: Int) {
		target match {
			case Some(Base) => {
				playerSpawnAction(source, _.addBaseDamage(xp))

				getTeam(source.getOrElse(sys.error("Who did did damage?")))
					.getOrElse(sys.error("Non team member damaged?")).modifyScore(xp)
			}
			case _ => playerSpawnAction(source, _.addOtherDamage(xp))
		}
	}

	def structureDestroy(source: Option[UUID], target: Option[Target], xp: Int) {
		target match {
			case Some(Base) => playerSpawnAction(source, _.addBaseDestroy(xp))
			case _ => playerSpawnAction(source, _.addOtherDestroy(xp))
		}
	}
}