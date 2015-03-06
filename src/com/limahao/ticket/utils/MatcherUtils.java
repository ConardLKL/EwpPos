package com.limahao.ticket.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtils {
	public static boolean isMobileNO(String mobiles) { // 严格的11位手机号码---正则
		Pattern p = Pattern
				.compile("^((13[0-9])|(147)|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isPasswordNo(String pwd) {// 匹配去掉空格6-20位
		Pattern pattern = Pattern.compile("^[^\\s]{6,20}$");
		Matcher matcher = pattern.matcher(pwd);
		return matcher.matches();
	}

	/**
	 * 正确邮箱方法
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		// "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		String strPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 判断是否是身份证的正则方法
	 * 
	 * @param idString
	 * @return
	 */
	public static boolean ischeckIdCard(String idString) {
		// 身份证正则表达式(15位)
		// String card1="^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$";
		String isIDCard1 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
		// 身份证正则表达式(18位)    ^(\\d{15}|\\d{17}[\\dxX])$
		//String isIDCard2 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";
		String isIDCard2 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}[\\dxX]$";
		Pattern p1 = Pattern.compile(isIDCard1);
		Matcher m1 = p1.matcher(idString);
		Pattern p2 = Pattern.compile(isIDCard2);
		Matcher m2 = p2.matcher(idString);
		if (idString.length() == 15) {
			return m1.matches();
		} else if (idString.length() == 18) {
			return m2.matches();
		}

		return false;
	}
}
