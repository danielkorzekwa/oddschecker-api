package dk.bot.oddschecker;

import java.text.ParseException;
import java.util.List;

import dk.bot.oddschecker.model.HorseWinMarket;
import dk.bot.oddschecker.model.HorseWinMarketRunner;

/** Adapter to the www.oddschecker.com
 * 
 * @author korzekwad
 *
 */
public interface OddsCheckerService {


	/** Returns the horse win markets without runners.
	 * @return
	 */
	public List<HorseWinMarket> getHorseWinMarkets();

	/**
	 * Returns horse win market runners with the longest runner odds (BetFair odds
	 * are not considered). Runners odds for the same market can be obtained
	 * from a different betting sites.
	 * 
	 * @return Null if market not found.
	 * @throws ParseException
	 */
	public List<HorseWinMarketRunner> getHorseWinMarkerRunners(String marketUrl);
	
	
}
