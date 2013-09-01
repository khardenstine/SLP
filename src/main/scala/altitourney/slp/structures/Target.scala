package altitourney.slp.structures

import altitourney.slp.SLP

sealed abstract class Target(val name: String)
case object Turret extends Target("turret")
case object Base extends Target("base")

object Target {
	def withName(name: String): Option[Target] = {
		name match {
			case Turret.name => Some(Turret)
			case Base.name => Some(Base)
			case _ =>
				SLP.getLog.warn("Unknown structure " + name)
				None
		}
	}
}
