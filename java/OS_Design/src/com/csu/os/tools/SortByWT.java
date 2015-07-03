package com.csu.os.tools;

import java.util.Comparator;

import com.csu.os.resource.PCB;

/**
 * 按响应比排序
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class SortByWT implements Comparator<PCB> {

	@Override
	public int compare(PCB o1, PCB o2) {
		// TODO Auto-generated method stub
		
		double wt1 = 0;//o1的响应比
		double wt2 = 0;//o2的响应比
		
		//计算wt1
		if(o1.getRunTime() > 0) {
			
			wt1 = 1 + o1.getWaitTime()/o1.getRunTime();
		} else {
			
			wt1 = 1 + o1.getWaitTime();
		}
		
		//计算wt2
		if(o2.getRunTime() > 0) {
			
			wt2 = 1 + o2.getWaitTime()/o2.getRunTime();
		} else {
			
			wt2 = 1 + o2.getWaitTime();
		}
		
		return wt1==wt2?0:(wt1<wt2?1:-1);
	}

}
