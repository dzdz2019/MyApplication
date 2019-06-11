package com.example.myapplication.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	// private static Log logger = LogFactory.getLog(StringUtil.class);
	// 国标码和区位码转换常量
	static final int GB_SP_DIFF = 160;
	// 存放国标一级汉字不同读音的起始区位码
	static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
			2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
			4086, 4390, 4558, 4684, 4925, 5249, 5600 };
	// 存放国标一级汉字不同读音的起始区位码对应读音
	static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
			'y', 'z' };
	// 获取一个字符串的拼音码
	public static String getFirstLetter(String oriStr) {
		try {
			String str = oriStr.toLowerCase();
			StringBuffer buffer = new StringBuffer();
			char ch;
			char[] temp;
			for (int i = 0; i < str.length(); i++) { // 依次处理str中每个字符
				ch = str.charAt(i);
				temp = new char[] { ch };
				byte[] uniCode = new String(temp).getBytes("GBK");
				if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
					buffer.append(temp);
				} else {
//				buffer.append(convert(uniCode));
					byte[] bytes=uniCode;
					char result = '-';
					int secPosValue = 0;
					int k;
					for (k = 0; k < bytes.length; k++) {
						bytes[k] -= GB_SP_DIFF;
					}
					secPosValue = bytes[0] * 100 + bytes[1];
					for (k = 0; k < 23; k++) {
						if (secPosValue >= secPosValueList.length && secPosValue < secPosValueList[k + 1]) {
							result = firstLetter[k]; // 无法识别的字符显示为'-'
							break;
						}
					}
					buffer.append(result);
				}
			}
			return buffer.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
	 * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
	 * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
	 
	static char convert(byte[] bytes) {
		char result = '-';
		int secPosValue = 0;
		int k;
		for (k = 0; k < bytes.length; k++) {
			bytes[k] -= GB_SP_DIFF;
		}
		secPosValue = bytes[0] * 100 + bytes[1];
		for (k = 0; k < 23; k++) {
			if (secPosValue >= secPosValueList.length && secPosValue < secPosValueList[k + 1]) {
				result = firstLetter[k]; // 无法识别的字符显示为'-'
				break;
			}
		}
		return result;
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean stringNotNull(String val)
	{
		boolean flag=false;
		if(val!=null && !val.trim().equals("") && !val.trim().equals("null"))
		{
			flag=true;
		}
		return flag;
	}
	
	/**
	 * 校验集合是否为空
	 *
	 * @param list 集合
	 * @return boolean 是否为空：true,不为空，false,为空
	 * @throws
	 */
	public static boolean listNotNull(List list)
	{
		if (null == list || list.size() == 0)
		{
			return false;
		}

		return true;
	}
	
	/**
	 * 验证是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
	{
		if(null == str)
		{
			return false;
		}
		
        String pattern = "^[1-9]+[0-9]*$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        
		return m.matches();
	}
	
	/**
	 * 验证是否是ip地址
	 * @param str
	 * @return
	 */
	public static boolean isIP(String str)
	{
		if(null == str)
		{
			return false;
		}
		
        String pattern = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        
		return m.matches();
	}
}
