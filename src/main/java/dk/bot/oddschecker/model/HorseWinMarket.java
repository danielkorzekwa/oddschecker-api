package dk.bot.oddschecker.model;

import java.util.Date;

/** Represents the OddsChecker win horse market.
 * 
 * @author korzekwad
 *
 */
public class HorseWinMarket {

	/** e.g. Kempton or Warwick*/
	private String meetingName;
	
	/** e.g. 03.11.2008 13:50*/
	private Date marketTime;
	
	/**e.g. http://www.oddschecker.com/horse-racing/plumpton/12:20/best-odds/*/
	private String marketUrl;
	
	public String getMeetingName() {
		return meetingName;
	}
	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}
	public Date getMarketTime() {
		return marketTime;
	}
	public void setMarketTime(Date marketTime) {
		this.marketTime = marketTime;
	}
	public String getMarketUrl() {
		return marketUrl;
	}
	public void setMarketUrl(String marketUrl) {
		this.marketUrl = marketUrl;
	}
}
