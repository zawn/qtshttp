package com.qnap.qdk.util;

/**
 * @author chuangjo
 *	String translator
 */
public class  StringTranslator{

	private static final String ezEncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final int[] ezDecodeChars = new int[]{
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
	    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
	    -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
	    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
	    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
	    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

	private static String utf16to8(String str){
		String out = "";
		for(int i = 0; i<str.length(); i++){
			char c = str.charAt(i);
			if((c >= 0x0001) && (c<=0x007F)){
				out += str.charAt(i);
			}else if(c > 0x07FF){
				out += Character.toString((char)(0xE0 | ((c >> 12) & 0x0F)));
				out += Character.toString((char)(0x80 | ((c >> 6) & 0x3F)));
				out += Character.toString((char)(0x80 | ((c >> 0) & 0x3F)));
			}else{
				out += Character.toString((char)(0xC0 | ((c >> 6) & 0x1F)));
				out += Character.toString((char)(0x80 | ((c >> 0) & 0x3F)));
			}
		}
		return out;
	}
	
	/**
	 * Password encode
	 * @param str Original password
	 * @return Translating password
	 */
	public static String ezEncode(String str){
		String utf16to8_result = utf16to8(str);
		String out = "";
		char c1, c2, c3;
		int i = 0;
		while(i < utf16to8_result.length()){
			c1 = (char)(utf16to8_result.charAt(i++) & 0xFF);
			if(i == utf16to8_result.length()){
				out += ezEncodeChars.charAt(c1 >> 2);
				out += ezEncodeChars.charAt((c1 & 0x3) << 4);
				out += "==";
				break;
			}
			c2 = utf16to8_result.charAt(i++);
			if(i == utf16to8_result.length()){
				out += ezEncodeChars.charAt(c1 >> 2);
			    out += ezEncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
			    out += ezEncodeChars.charAt((c2 & 0xF) << 2);
			    out += "=";
			    break;
			}
			c3 = utf16to8_result.charAt(i++);
			out += ezEncodeChars.charAt(c1 >> 2);
			out += ezEncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
			out += ezEncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));
			out += ezEncodeChars.charAt(c3 & 0x3F);
		}
		return out;
	}
	
	/**
	 * Replace blank
	 * @param str Original string
	 * @return Translating string
	 */
	public static String replaceBlank(String str) {
		if(str.contains("+")) {
			str = str.replace("+", "%20");
		}
		return str;
	}
}
