package com.csu.os.resource;

import com.csu.os.managers.CPUManager;

import junit.framework.TestCase;

public class Test extends TestCase {

	public void testFCFS() {
		
		
	}
	
	public static void main(String[] args) {
		
		CPUManager cpuManager = new CPUManager();
		cpuManager.getPcbManager().setArithmeticStatus(4);
		for(int i=0; i<10; i++) {
			cpuManager.getPcbManager().addPCB();
		}
		cpuManager.start();

	}
}
