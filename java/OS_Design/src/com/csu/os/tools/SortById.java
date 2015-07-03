package com.csu.os.tools;

import java.util.Comparator;

import com.csu.os.resource.PCB;

public class SortById implements Comparator<PCB> {

	@Override
	public int compare(PCB o1, PCB o2) {
		// TODO Auto-generated method stub
		return o1.getLevel()==o2.getLevel()?0:(o1.getLevel()<o2.getLevel()?1:-1);
	}
	
}
