package dk.bot.oddschecker;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import dk.bot.oddschecker.model.HorseWinMarket;
import dk.bot.oddschecker.model.HorseWinMarketRunner;

public class OddsCheckerServiceImplIntegrationTest {

	@Test
	public void testGetHorseWinMarkets() {

		OddsCheckerServiceImpl oddsCheckerService = new OddsCheckerServiceImpl();

		List<HorseWinMarket> markets = oddsCheckerService.getHorseWinMarkets();
		assertTrue(markets.size() > 0);
	}

	@Test
	public void testGetHorseWinMarkerRunners() {
		OddsCheckerServiceImpl oddsCheckerService = new OddsCheckerServiceImpl();

		List<HorseWinMarket> markets = oddsCheckerService.getHorseWinMarkets();
		assertTrue(markets.size() > 0);

		List<HorseWinMarketRunner> runners = oddsCheckerService.getHorseWinMarkerRunners(markets.get(0).getMarketUrl());
		assertTrue(runners.size() > 0);

	}
}
