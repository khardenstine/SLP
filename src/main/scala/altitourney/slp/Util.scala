package altitourney.slp

import java.util.UUID
import org.joda.time.DateTime
import java.sql.PreparedStatement

object Util {
	def generateHash(player: UUID): String = {
		val date = new DateTime()
		val seed = (date.getDayOfMonth + date.getMonthOfYear + player.toString).getBytes.map(_ << 2).sum
		new String(new util.Random(seed).alphanumeric.take(4).toArray)
	}

	def listToQuestionMarks[A<: Any](i: Iterable[A]): String = {
		i.map(_ => "?").mkString(", ")
	}

	def setListOnStatement[A<: Any](i: Iterable[A], stmt: PreparedStatement): Unit = {
		i.zipWithIndex.foreach( v => stmt.setString(v._2, v._1.toString))
	}
}
