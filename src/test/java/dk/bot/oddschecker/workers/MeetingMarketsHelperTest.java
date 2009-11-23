package dk.bot.oddschecker.workers;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

public class MeetingMarketsHelperTest {

	@Test
	public void testGetMeetingDetails() throws ParseException {

		Calendar today = GregorianCalendar.getInstance();
		today.setTimeInMillis(System.currentTimeMillis());
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		assertEquals("lingfield", MeetingMarketsHelper.getMeetingDetails("/horse-racing/lingfield/")[0]);
		assertEquals(today.getTime(), MeetingMarketsHelper.getMeetingDetails("/horse-racing/lingfield/")[1]);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");

		assertEquals("towcester", MeetingMarketsHelper.getMeetingDetails("/horse-racing/2008-11-23-towcester/")[0]);
		assertEquals(dateFormat.parse("2008-11-23"), MeetingMarketsHelper.getMeetingDetails("/horse-racing/2008-11-23-towcester/")[1]);

		assertEquals("gowran-park", MeetingMarketsHelper.getMeetingDetails("/horse-racing/2008-11-23-gowran-park/")[0]);
		assertEquals(dateFormat.parse("2008-11-26"), MeetingMarketsHelper.getMeetingDetails("/horse-racing/2008-11-26-gowran-park/")[1]);

	}

	@Test
	public void testGetMeetingTime() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d H:m");
		assertEquals(dateFormat.parse("2008-11-23 12:30"),MeetingMarketsHelper.getMeetingTime(dateFormat.parse("2008-11-23 00:00"),"12:30"));
	}
}
