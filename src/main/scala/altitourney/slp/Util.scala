package altitourney.slp

import java.util.UUID
import org.joda.time.DateTime

object Util {
	def generateHash(player: UUID): String = {
		val date = new DateTime()
		val seed = (date.getDayOfMonth + date.getMonthOfYear + player.toString).getBytes.map(_ << 2).sum
		new String(new util.Random(seed).alphanumeric.take(4).toArray)
	}
}
