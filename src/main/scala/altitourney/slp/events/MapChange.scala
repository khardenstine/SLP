package altitourney.slp.events

import play.api.libs.json.JsValue
import altitourney.slp.games._
import altitourney.slp.SLP
import java.util.UUID
import org.joda.time.DateTime

/**
 * {"port":27276,"leftTeam":3,"time":3898,"rightTeam":4,"map":"tbd_lostcity","type":"mapChange","mode":"tbd"}
 */
class MapChange(jsVal: JsValue) extends EventHandler(jsVal) {
	getSharedEventData.setGame(getGameType(getTime, getString("map"), getInt("leftTeam"), getInt("rightTeam")))
	SLP.executeDBStatement(
		"""
		  |INSERT INTO maps SELECT '%1$s', '%2$s', (SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE') WHERE NOT EXISTS (SELECT 1 FROM maps WHERE name='%2$s' AND mode_dict=(SELECT dict_id FROM dicts WHERE dict_value = '%3$s' AND dict_type = 'MODE'));
		""".stripMargin.format(UUID.randomUUID().toString, getString("map"), getString("mode"))
	)

	def getGameType(dateTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Game = {
		if (map == SLP.getLobbyMap)
		{
			new NoGame(dateTime, map, leftTeamId, rightTeamId)
		} else {
			getString("mode") match {
				case "ball" => new BallGame(dateTime, map, leftTeamId, rightTeamId)
				case "tbd" => new TBDGame(dateTime, map, leftTeamId, rightTeamId)
				case _ => new NoGame(dateTime, map, leftTeamId, rightTeamId)
			}
		}
	}
}
