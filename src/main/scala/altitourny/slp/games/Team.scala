package altitourny.slp.games

import java.util.UUID
import scala.collection.mutable

class Team (final val id: Int) {
	val players: mutable.Set[UUID] = mutable.Set()
	private var score: Int = 0

	def modifyScore(mod: Int) = {
		this.synchronized(
		     score += mod
		)
	}

	def getScore = score
}
