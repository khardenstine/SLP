package altitourney.slp.games

import org.joda.time.Duration

class PerkData(
				  val kills: Int,
				  val assists: Int,
				  val deaths: Int,
				  val exp: Int,
				  val goals: Int,
				  val goalAssists: Int,
				  val goalSecondaryAssists: Int,
				  val baseDamage: Int,
				  val otherDamage: Int,
				  val baseDestroys: Int,
				  val otherDestroys: Int,
				  val timeAlive: Duration
				  ) {
	def + (perkData: Option[PerkData]) : PerkData = {
		perkData.fold[PerkData](this){pd: PerkData =>
			new PerkData(pd.kills + kills, pd.assists + assists, pd.deaths + deaths, pd.exp + exp,
				pd.goals + goals, pd.goalAssists + goalAssists, pd.goalSecondaryAssists + goalSecondaryAssists,
				pd.baseDamage + baseDamage, pd.otherDamage + otherDamage,
				pd.baseDestroys + baseDestroys, pd.otherDestroys + otherDestroys,
				pd.timeAlive.plus(timeAlive)
			)
		}
	}
}
