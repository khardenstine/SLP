package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":8838,"type":"pingSummary","pingByPlayer":{"5":0}}
 */
case class PingSummary(jsVal: JsValue) extends EventHandler(jsVal) {

}
