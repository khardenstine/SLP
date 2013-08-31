package altitourney.slp.events

import altitourney.slp.SLP
import altitourney.slp.games.Mode
import java.util.UUID
import play.api.libs.json.JsValue

/**
 * {"port":27276,"leftTeam":3,"time":3898,"rightTeam":4,"map":"tbd_lostcity","type":"mapChange","mode":"tbd"}
 */
class MapChange(jsVal: JsValue) extends EventHandler(jsVal) {
	val map = getString("map")
	val mode = Mode.withName(getString("mode"))

	getServerContext.newGame(mode, getTime, map, getInt("leftTeam"), getInt("rightTeam"))

	val query =
		"""
		  |INSERT INTO maps
		  |SELECT ?, ?,
		  |       (SELECT dict_id
		  |        FROM   dicts
		  |        WHERE  dict_value = ?
		  |               AND dict_type = 'MODE')
		  |WHERE  NOT EXISTS (SELECT 1
		  |                   FROM   maps
		  |                   WHERE  name = ?
		  |                          AND mode_dict = (SELECT dict_id
		  |                                           FROM   dicts
		  |                                           WHERE  dict_value = ?
		  |                                                  AND dict_type = 'MODE'));
		""".stripMargin

	SLP.preparedStatement(query){
		stmt =>
			stmt.setString(1, UUID.randomUUID().toString)
			stmt.setString(2, map)
			stmt.setString(3, mode.name)
			stmt.setString(4, map)
			stmt.setString(5, mode.name)
	}
}
