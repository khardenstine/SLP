package altitourney.slp.games

import java.util.UUID

case class RatingsChange(oldRating: Int, newRating: Int)

case class PlayerRatingsChange(player: UUID, oldRating: Int, newRating: Int)
