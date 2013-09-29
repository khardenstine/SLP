package altitourney.slp

import java.sql.PreparedStatement
import java.util.UUID
import org.joda.time.{Duration, DateTime}
import org.joda.time.format.{DateTimeFormat, PeriodFormatterBuilder}

object Util {
	final val PERIOD_FORMATTER = new PeriodFormatterBuilder()
		.appendHours
		.appendSeparator(":")
		.minimumPrintedDigits(2)
		.appendMinutes
		.appendSeparator(":")
		.appendSeconds
		.toFormatter

	final val TIME_FORMAT = "hh:mm aa ZZ"

	def formatFor(dateTime: DateTime, formatString: String): String = {
		DateTimeFormat.forPattern(formatString).print(dateTime)
	}

	def getFormattedTimeDifference(start: DateTime, end: DateTime = new DateTime()): String = {
		PERIOD_FORMATTER.print(new Duration(start, end).toPeriod)
	}

	def generateHash(player: UUID): String = {
		val date = new DateTime()
		val seed = (date.getDayOfMonth + date.getMonthOfYear + player.toString).getBytes.map(_ << 2).sum
		new String(new util.Random(seed).alphanumeric.take(4).toArray)
	}

	def listToQuestionMarks[A<: Any](i: Iterable[A]): String = {
		i.map(_ => "?").mkString(", ")
	}

	def setListOnStatement[A<: Any](i: Iterable[A], stmt: PreparedStatement): Unit = {
		i.zipWithIndex.foreach( v => stmt.setString(v._2 + 1, v._1.toString))
	}

	def setOptionalInt(index: Int, op: Option[Int])(implicit stmt: PreparedStatement): Unit = {
		setOption(index, op, java.sql.Types.INTEGER)
	}

	protected def setOption(index: Int, op: Option[Any], sqlType: Int)(implicit stmt: PreparedStatement): Unit = {
		op match {
			case Some(v) => stmt.setObject(index, v, sqlType)
			case None => stmt.setNull(index, sqlType)
		}
	}
}
