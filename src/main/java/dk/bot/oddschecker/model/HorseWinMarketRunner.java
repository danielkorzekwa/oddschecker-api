package dk.bot.oddschecker.model;

/** Horse race runner.
 * 
 * @author korzekwad
 *
 */
public class HorseWinMarketRunner {

	private String runnerName;
	
	private double longestOdds;

	public String getRunnerName() {
		return runnerName;
	}

	public void setRunnerName(String runnerName) {
		this.runnerName = runnerName;
	}

	public double getLongestOdds() {
		return longestOdds;
	}

	public void setLongestOdds(double longestOdds) {
		this.longestOdds = longestOdds;
	}

}
