package altitourney.slp.events

import play.api.libs.json.JsValue
import altitourney.slp.games._
import altitourney.slp.SLP
import java.util.UUID

/**
 * {"port":27276,"leftTeam":3,"time":3898,"rightTeam":4,"map":"tbd_lostcity","type":"mapChange","mode":"tbd"}
 */
class MapChange(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.executeDBStatement(
		"""
		  |INSERT INTO maps SELECT '%1$s', '%2$s', (SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE') WHERE NOT EXISTS (SELECT 1 FROM maps WHERE name='%2$s' AND mode_dict=(SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE'));
		""".stripMargin.format(UUID.randomUUID().toString, getString("map"), getString("mode"))
	)
	getString("mode") match {
		case "ball" => getSharedEventData.setGame(new BallGame(getTime, getString("map"), getInt("leftTeam"), getInt("rightTeam")))
		case "tbd" => getSharedEventData.setGame(new NoGame)//new TBDGame(getTime, getString("map"), getInt("leftTeam"), getInt("rightTeam")))
		case _ => getSharedEventData.setGame(new NoGame)
	}
}
