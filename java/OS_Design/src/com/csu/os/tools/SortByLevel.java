package com.csu.os.tools;

import java.util.Comparator;

import com.csu.os.resource.PCB;

/**
 * 按优先级排序
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class SortByLevel implements Comparator<PCB> {

	@Override
	public int compare(PCB o1, PCB o2) {
		// TODO Auto-generated method stub
		return o1.getLevel()==o2.getLevel()?0:(o1.getLevel()<o2.getLevel()?1:-1);
	}

}
