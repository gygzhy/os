package com.csu.os.tools;

import java.util.Random;

/**
 * ������
 * �������ֳ��÷���
 * @author GYGZHY
 * @date   2015��7��3��
 */
public class Tools {

	/**
	 * ��������ַ�������
	 * @param length �ַ�������
	 * @return ����������ַ���
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
	 * ����ָ����Χ�ڵ�����������
	 * @param min ��Χ����
	 * @param max ��Χ����
	 * @return �����������
	 */
	public static int getRandomInteger(int min, int max) {
		
		Random random = new Random();
		int number = random.nextInt(max)%(max-min+1) + min;
		
		return number;
		
	}
	
}
