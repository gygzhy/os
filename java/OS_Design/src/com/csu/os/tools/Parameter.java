package com.csu.os.tools;

/**
 * 参数类
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class Parameter {
	
	public static int TIME_SLICE = 1;//轮转时间片大小
	public static int SLEEP_TIME = 1000; //时间片产生频率
	public static int MAX_READY_NUMBER = 100;//就绪队列所允许的最大进程数
	public static int LEVEL_CHANGE = 1;//动态优先级算法中，优先级改变的大小
	
}
