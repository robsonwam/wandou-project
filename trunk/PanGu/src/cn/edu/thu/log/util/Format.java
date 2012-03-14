package cn.edu.thu.log.util;

import java.util.ArrayList;
import java.util.Date;

public class Format {
	/**
	 * setup timestamp in Date format
	 * 
	 * @param timeString
	 *            String of timeStamp
	 * @return Date format as timeStamp
	 */

	public static Date StringToTimeStamp(String timeString) {
		Date timeStamp = new Date();
		if (timeString != null) {
			try {
				timeString = timeString.trim();
				timeStamp
						.setYear(Integer.parseInt(timeString.substring(0, 4)) - 1900);
				timeStamp
						.setMonth(Integer.parseInt(timeString.substring(4, 6)) - 1);
				timeStamp.setDate(Integer.parseInt(timeString.substring(6, 8)));
				timeStamp
						.setHours(Integer.parseInt(timeString.substring(8, 10)));
				timeStamp.setMinutes(Integer.parseInt(timeString.substring(10,
						12)));
				timeStamp.setSeconds(Integer.parseInt(timeString.substring(12,
						14)));
			} catch (Exception e) {
				System.out.print("\ntime format wrong as:" + timeString);

			}
		}
		return timeStamp;
	}
	/**
	 * split content by Tokenizer to List
	 * 
	 * @param logHeadTokenizer
	 *            Tokenizer
	 * @param logHeadContent
	 *            content
	 * @return a list of content fields
	 */
	public static ArrayList<String> splitContentToList(String tokenizer,
			String content) {
		// Split logHeadContent by logHeadTokenizer
		int len = content.length();
		int pos = 0;
		ArrayList<String> alParams = new ArrayList<String>();
		int nl = tokenizer.length();
		int oldPos = 0;
		while (pos < len) {
			int idx = content.indexOf(tokenizer, pos);
			if (idx != -1) {
				pos = idx + nl;

				if (idx == 0) {
					alParams.add("");
					oldPos = pos;
				} else if (content.charAt(idx - 1) != '\\') {
					alParams.add(content.substring(oldPos, idx));
					oldPos = pos;
				}

				if (pos == len)
					alParams.add("");
			} else {
				alParams.add(content.substring(pos + nl));
				pos = len;
			}
		}
		return alParams;
	}
}
