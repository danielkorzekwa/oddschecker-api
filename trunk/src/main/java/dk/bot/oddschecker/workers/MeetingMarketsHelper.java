package dk.bot.oddschecker.workers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import dk.bot.oddschecker.util.ParseHelper;

public class MeetingMarketsHelper {

	/**
	 * Returns meeting name and meeting day. Meeting formats: 1 -
	 * /horse-racing/meetingName, 2 /horse-racing/yyyy-MM-DD-meetingName
	 * 
	 * 
	 * @param meetingUrl
	 * @return 0 - meetingName, 1 - meetingDay
	 * @throws ParseException
	 */
	public static Object[] getMeetingDetails(String meetingUrl) throws ParseException {

		String[] split = meetingUrl.split("/");
		if (split.length != 3) {
			throw new ParseException("Wrong format of meetingUrl", 0);
		}
		String meetingString = split[2];

		Date meetingDay;
		String meetingName;

		String meetingDayString = ParseHelper.findRegex("(\\d+-\\d+-\\d+)-(.+)", meetingString, 1);
		if (meetingDayString != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
			meetingDay = dateFormat.parse(meetingDayString + " ");
			meetingName = ParseHelper.findRegex("(\\d+-\\d+-\\d+)-(.+)", meetingString, 2);
		} else {
			Calendar today = GregorianCalendar.getInstance();
			today.setTimeInMillis(System.currentTimeMillis());
			today.set(Calendar.HOUR, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			meetingDay = today.getTime();
			meetingName = meetingString;
		}

		Object[] obj = new Object[2];
		obj[0] = meetingName;
		obj[1] = meetingDay;

		return obj;
	}
	
	/**
	 * Add meeting hour to meetingDay, e.g. 2008-11-03 + 12:30 = 2008-11-03
	 * 12:30
	 * 
	 * @param meetingDay
	 * @param meetingHour
	 *            hh:mm, e.g. 12:30
	 * @return
	 * @throws ParseException
	 */
	public static Date getMeetingTime(Date meetingDay, String meetingHour) throws ParseException {
		String[] hhmm = meetingHour.split(":");
		if (hhmm.length != 2) {
			throw new ParseException("Wrong format of meeting hour:" + meetingHour, 0);
		}

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(meetingDay);
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hhmm[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(hhmm[1]));

		return calendar.getTime();
	}
}
