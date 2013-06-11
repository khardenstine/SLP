package altitourny.slp.log

abstract class LogLevel(final val value: Int) {
	def shouldLog(level: LogLevel): Boolean = {
		this.value >= level.value
	}
}

object LogLevel {
	def valueOf(str: String): LogLevel = {
		str match {
			case "OFF" => OFF
			case "ERROR" => ERROR
			case "WARN" => WARN
			case "INFO" => INFO
			case "DEBUG" => DEBUG
			case _ => throw new RuntimeException("Invalid Log Level")
		}
	}
}

case object OFF extends LogLevel(0)

case object ERROR extends LogLevel(1)

case object WARN extends LogLevel(2)

case object INFO extends LogLevel(3)

case object DEBUG extends LogLevel(4)