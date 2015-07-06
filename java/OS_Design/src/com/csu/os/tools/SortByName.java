package com.csu.os.tools;

import java.util.Comparator;

import com.csu.os.resource.PCB;

public class SortByName implements Comparator<PCB> {

	@Override
	public int compare(PCB o1, PCB o2) {
		// TODO Auto-generated method stub
		if(o1.getName().compareTo(o2.getName()) != 0) {
			
			return o1.getName().compareTo(o2.getName());
		
		} else {
			
			return o1.getpId().toString().compareTo(o2.getpId().toString());
		}
	}

}
