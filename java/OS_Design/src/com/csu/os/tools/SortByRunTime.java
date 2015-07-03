package com.csu.os.tools;

import java.util.Comparator;

import com.csu.os.resource.PCB;

/**
 * 按执行时间排序
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class SortByRunTime implements Comparator<PCB> {

	@Override
	public int compare(PCB o1, PCB o2) {
		// TODO Auto-generated method stub
		return o1.getRunTime()==o2.getRunTime()?0:(o1.getRunTime()>o2.getRunTime()?1:-1);
	}
	
}
