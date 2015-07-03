package com.csu.os.resource;

import com.csu.os.managers.CPUManager;
import com.csu.os.managers.PCBManager;

import junit.framework.TestCase;

public class Test extends TestCase {

	public void testFCFS() {
		
		
	}
	
	public static void main(String[] args) {
		
		CPUManager cpuManager = new CPUManager();
		cpuManager.start();
	}
}
