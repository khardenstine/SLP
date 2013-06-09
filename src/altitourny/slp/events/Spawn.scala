package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":13919,"plane":"Explodet","player":5,"perkRed":"Remote Mine","perkGreen":"Flexible Wings",
 * "team":3,"type":"spawn","perkBlue":"Turbocharger","skin":"No Skin"}
 */
case class Spawn(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object Spawn extends Event
{
	val logType = "spawn"

	def getEventHandler(jsVal: JsValue)
	{
		new Spawn(jsVal)
	}
}
