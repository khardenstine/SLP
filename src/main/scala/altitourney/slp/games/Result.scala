package altitourney.slp.games

sealed abstract class Result(val logval: String)
case object Tie extends Result("tie")
case object Decisive extends Result("decisive")

object Result {
	def withName(name: String): Option[Result] = {
		name match {
			case Tie.logval => Some(Tie)
			case Decisive.logval => Some(Decisive)
			case _ => None
		}
	}
}
