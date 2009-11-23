package dk.bot.oddschecker.workers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import dk.bot.oddschecker.model.HorseWinMarket;
import dk.bot.oddschecker.util.ParseHelper;

/**
 * Returns list of meeting markets.
 * 
 * @author daniel
 * 
 */
public class MeetingMarketsWorker implements Runnable {

	private final CountDownLatch doneSignal;
	private final HttpClient client;
	private final String oddsCheckerUrl;
	private final String meetingUrl;

	private boolean executionSuccess = true;
	private List<HorseWinMarket> markets = new ArrayList<HorseWinMarket>();

	public MeetingMarketsWorker(CountDownLatch doneSignal, HttpClient client, String oddsCheckerUrl, String meetingUrl) {
		this.doneSignal = doneSignal;
		this.client = client;
		this.oddsCheckerUrl = oddsCheckerUrl;
		this.meetingUrl = meetingUrl;
	}

	public void run() {
		try {
			doWork();
		} catch (Exception e) {
			executionSuccess = false;
		} finally {
			doneSignal.countDown();
		}
	}

	private void doWork() throws ParseException, HttpException, IOException {

		Object[] meetingDetails = MeetingMarketsHelper.getMeetingDetails(meetingUrl);
		List<String> timeUrls = getMeetingsTimeUrls(client, meetingUrl);

		for (String timeUrl : timeUrls) {

			HorseWinMarket horseMarket = new HorseWinMarket();
			horseMarket.setMeetingName((String) meetingDetails[0]);
			horseMarket.setMarketTime(MeetingMarketsHelper.getMeetingTime((Date) meetingDetails[1], timeUrl));
			horseMarket.setMarketUrl(oddsCheckerUrl + meetingUrl + timeUrl + "/best-odds");

			markets.add(horseMarket);

		}
	}

	

	/**
	 * 
	 * @param client
	 * @return list of meeting time urls, e.g. /14:20, /14:30
	 * @throws IOException 
	 * @throws HttpException 
	 */
	private List<String> getMeetingsTimeUrls(HttpClient client, String meetingUrl) throws HttpException, IOException {
		// Create a method instance.
		GetMethod method = new GetMethod(oddsCheckerUrl + meetingUrl);

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);
			
			if (statusCode != HttpStatus.SC_OK) {
				throw new IOException("GET error: " + statusCode);
			}

			// Read the response body.
			String responseBody = method.getResponseBodyAsString(Integer.MAX_VALUE);

			String[] timeStrings = responseBody.split("<table id=\"racesTable")[1]
					.split("<div class=\"contentNF1container\">")[0].split("<tr class=\"hr_header\">");
			List<String> timeUrls = new ArrayList<String>();

			for (int i = 1; i < timeStrings.length; i++) {

				String timeString = timeStrings[i];
				String hour = ParseHelper.findRegex(".+" + meetingUrl + "(\\d+):(\\d+)\">.+", timeString, 1);
				String minute = ParseHelper.findRegex(".+" + meetingUrl + "(\\d+):(\\d+)\">.+", timeString, 2);

				if (hour != null && minute != null) {
					timeUrls.add(hour + ":" + minute);
				}
			}

			return timeUrls;

		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

	

	public boolean isExecutionSuccess() {
		return executionSuccess;
	}

	public List<HorseWinMarket> getMarkets() {
		return markets;
	}
}
