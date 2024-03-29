package altitourney.slp.events

import java.util.UUID
import play.api.libs.json.JsValue

/**
 *
 * {"port":27277,"time":3697424,"nicknames":["IL|Carlos98"],"vaporIds":["79b7a12f-12b4-46ab-adae-580131833b88"],
 * "playerIds":[0],"ips":["192.168.1.3:27272"],"tournamentInProgress":false,"type":"logServerStatus"}
 */
class LogServerStatus(jsVal: JsValue) extends EventHandler(jsVal){
	val nickNames = (jsVal \ "nicknames").as[Seq[JsValue]].map(_.as[String])
	val vapors = (jsVal \ "vaporIds").as[Seq[JsValue]]map(v => UUID.fromString(v.as[String]))
	val playerIds = (jsVal \ "playerIds").as[Seq[JsValue]].map(pid => pid.as[Int])

	getServerContext.clearPlayers()
	nickNames.zip(vapors).zip(playerIds).foreach { truple =>
		getServerContext.addPlayer(truple._1._2, truple._2, truple._1._1)
	}
}
