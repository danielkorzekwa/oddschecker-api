package dk.bot.oddschecker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHelper {

	/**
	 * 
	 * @param pattern
	 * @param sequence
	 * @param groupNumber
	 * @return null if not matched
	 */
	public static String findRegex(String pattern, String sequence, int groupNumber) {
		Pattern teamPattern = Pattern.compile(pattern, Pattern.DOTALL);

		Matcher matcher = teamPattern.matcher(sequence);
		if (matcher.matches()) {
			return matcher.group(groupNumber);
		} else {
			return null;
		}

	}
}
