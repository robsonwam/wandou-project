package cn.edu.thu.log.test.cleantest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testMatcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pattern pattern=Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher matcher=pattern.matcher("10.10.128.58");
		System.out.println("是否匹配："+matcher.matches());
	}

}
