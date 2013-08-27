package altitourney.slp.events.consoleCommands

import altitourney.slp.SLP
import altitourney.slp.events.EventHandler
import play.api.libs.json.JsValue

class Ladder(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.getRegistryFactory.getLadderRegistry.handle(jsVal)
}
