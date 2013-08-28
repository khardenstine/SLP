package altitourney.slp.events

import altitourney.slp.SLP
import altitourney.slp.games._
import java.util.UUID
import org.joda.time.DateTime
import play.api.libs.json.JsValue

/**
 * {"port":27276,"leftTeam":3,"time":3898,"rightTeam":4,"map":"tbd_lostcity","type":"mapChange","mode":"tbd"}
 */
class MapChange(jsVal: JsValue) extends EventHandler(jsVal) {
	getServerContext.newGame(Mode.withName(getString("mode")), getTime, getString("map"), getInt("leftTeam"), getInt("rightTeam"))
	SLP.executeDBStatement(
		"""
		  |INSERT INTO maps SELECT '%1$s', '%2$s', (SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE') WHERE NOT EXISTS (SELECT 1 FROM maps WHERE name='%2$s' AND mode_dict=(SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE'));
		""".stripMargin.format(UUID.randomUUID().toString, getString("map"), getString("mode"))
	)
}
