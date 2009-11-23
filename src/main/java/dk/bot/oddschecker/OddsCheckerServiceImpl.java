package dk.bot.oddschecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import dk.bot.oddschecker.model.HorseWinMarket;
import dk.bot.oddschecker.model.HorseWinMarketRunner;
import dk.bot.oddschecker.util.ParseHelper;
import dk.bot.oddschecker.workers.MeetingMarketsWorker;

public class OddsCheckerServiceImpl implements OddsCheckerService {

	private final static String ODDS_CHECKER_GET_MEETINGS = "http://www.oddschecker.com/";

	private int successExecutions = 0;
	private int failureExecutions = 0;

	private HttpClient client;

	private ThreadPoolTaskExecutor executor;

	private boolean currentExecutionSuccess = true;

	public OddsCheckerServiceImpl() {
		executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.initialize();

		MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		httpConnectionManager.setMaxConnectionsPerHost(5);
		httpConnectionManager.setMaxTotalConnections(5);
		client = new HttpClient(httpConnectionManager);
		client.getParams().setSoTimeout(10000);
	}

	public List<HorseWinMarket> getHorseWinMarkets() {

		currentExecutionSuccess = true;

		List<HorseWinMarket> markets = new ArrayList<HorseWinMarket>();

		String[] meetingsUrls = getMeetingsUrls(client);

		CountDownLatch doneSignal = new CountDownLatch(meetingsUrls.length);
		List<MeetingMarketsWorker> workers = new ArrayList<MeetingMarketsWorker>();
		for (String meetingUrl : meetingsUrls) {
			MeetingMarketsWorker worker = new MeetingMarketsWorker(doneSignal, client, ODDS_CHECKER_GET_MEETINGS, meetingUrl);
			workers.add(worker);
			executor.execute(worker);
		}
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (MeetingMarketsWorker workerMarkets : workers) {
			if(!workerMarkets.isExecutionSuccess()) {
				currentExecutionSuccess=false;
			}
			markets.addAll(workerMarkets.getMarkets());
		}

		if (currentExecutionSuccess) {
			successExecutions++;
		} else {
			failureExecutions++;
		}

		return markets;
	}

	public List<HorseWinMarketRunner> getHorseWinMarkerRunners(String marketUrl) {

		// Create a method instance.
		GetMethod method = new GetMethod(marketUrl);
		method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		method.setRequestHeader("Cookie", "odds_type=decimal");

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				failureExecutions++;
				return null;
			}

			// Read the response body.
			String responseBody = method.getResponseBodyAsString(Integer.MAX_VALUE);

			String[] runnerStrings = responseBody.split("<table class=\"eventTable\">")[1].split("</table>")[0]
					.split("tr class=\"&#10;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;eventTableRow");

			List<HorseWinMarketRunner> runners = new ArrayList<HorseWinMarketRunner>();

			for (int i = 1; i < runnerStrings.length; i++) {

				String runnerString[] = runnerStrings[i].split("<td class=\"selections\">")[1].split("<td");

				String runnerName = ParseHelper.findRegex(".+;\">(.+)</a></span></td>", runnerString[0], 1);
				String longestOddString = ParseHelper.findRegex(".+;\">(.+)</td>", runnerString[1], 1);
				if (longestOddString == null) {
					longestOddString = "1000";
				}

				HorseWinMarketRunner runner = new HorseWinMarketRunner();
				runner.setRunnerName(runnerName);

				try {
					runner.setLongestOdds(Double.parseDouble(longestOddString));
				} catch (NumberFormatException e) {
					runner.setLongestOdds(1000);
				}
				runners.add(runner);
			}

			successExecutions++;
			return runners;

		} catch (HttpException e) {
			failureExecutions++;
			return null;
		} catch (IOException e) {
			failureExecutions++;
			return null;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

	/**
	 * 
	 * @param client
	 * @return list of meeting urls, e.g. /horse-racing/2008-11-20-thurles/ or
	 *         /horse-racing/kempton/
	 */
	private String[] getMeetingsUrls(HttpClient client) {
		// Create a method instance.
		GetMethod method = new GetMethod(ODDS_CHECKER_GET_MEETINGS);

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				currentExecutionSuccess = false;
				return new String[0];
			}

			// Read the response body.
			String responseBody = method.getResponseBodyAsString(Integer.MAX_VALUE);

			String[] meetingStrings = responseBody.split("a href=\"/horse-racing/racing-coupon\">Racing Coupon</a>")[1]
					.split("<a href=\"javascript:showSubMenu")[0].split("<a href=\"");
			String[] meetingsUrls = new String[meetingStrings.length - 1];

			for (int i = 1; i < meetingStrings.length; i++) {

				String meetingString = meetingStrings[i];
				String meetingUrl = ParseHelper.findRegex("(.+)\">.+</a>.*", meetingString, 1);

				meetingsUrls[i - 1] = meetingUrl;
			}

			return meetingsUrls;

		} catch (HttpException e) {
			currentExecutionSuccess = false;
			return new String[0];
		} catch (IOException e) {
			currentExecutionSuccess = false;
			return new String[0];
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

	public int getSuccessExecutions() {
		return successExecutions;
	}

	public int getFailureExecutions() {
		return failureExecutions;
	}

}
