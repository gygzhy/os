package com.csu.os.tools;

import java.util.Random;

/**
 * 工具类
 * 包含各种常用方法
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class Tools {

	/**
	 * 产生随机字符串方法
	 * @param length 字符串长度
	 * @return 随机产生的字符串
	 */
	public static String getRandomString(int length) {
		
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();  
	    
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString(); 
	    
	 }  
	
	
	/**
	 * 产生指定范围内的随机整数随机
	 * @param min 范围下限
	 * @param max 范围上限
	 * @return 产生的随机数
	 */
	public static int getRandomInteger(int min, int max) {
		
		Random random = new Random();
		int number = random.nextInt(max)%(max-min+1) + min;
		
		return number;
		
	}
	
}
