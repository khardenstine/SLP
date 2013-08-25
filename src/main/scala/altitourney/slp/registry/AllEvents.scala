package altitourney.slp.registry

import altitourney.slp.events._
import play.api.libs.json.JsValue

protected class AllEvents extends EventRegistry{
	val REGISTRY: Seq[REGISTER] =
		(new StartUpRegistry).REGISTRY ++ Seq(
			("assist",					new Assist(_)),
			("clientAdd",				new ClientAdd(_)),
			("clientNicknameChange",	new ClientNicknameChange(_)),
			("clientRemove",			new ClientRemove(_)),
			("consoleCommandExecute",	new ConsoleCommandExecute(_)),
			("gaol",					new Goal(_)),
			("kill",					new Kill(_)),
			("mapChange",				new MapChange(_)),
			("mapLoading",				new MapLoading(_)),
			("pingSummary",				new PingSummary(_)),
			("powerupAutoUse",			new PowerupAutoUse(_)),
			("powerupDefuse",			new PowerupDefuse(_)),
			("powerupPickup",			new PowerupPickup(_)),
			("powerupUse",				new PowerupUse(_)),
			("roundEnd",				new RoundEnd(_)),
			("serverStart",				new ServerStart(_)),
			("spawn",					new Spawn(_)),
			("structureDamage",			new StructureDamage(_)),
			("structureDestroy",		new StructureDestroy(_)),
			("teamChange",				new TeamChange(_))
		)

	def getFilter(jsVal: JsValue): String = (jsVal \ "type").as[String]
}