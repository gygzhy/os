package com.csu.os.resource;

import com.csu.os.managers.PCBManager;

import junit.framework.TestCase;

public class Test extends TestCase {

	public void testFCFS() {
		
		
	}
	
	public static void main(String[] args) {
		
		PCBManager pManager = new PCBManager();
		for(int i=0; i<1000; i++) {
			pManager.addPCB();
		}
		pManager.setArithmeticStatus(5);
		Thread thread = new Thread(pManager);
		thread.run();
	}
}
