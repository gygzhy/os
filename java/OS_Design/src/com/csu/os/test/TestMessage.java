package com.csu.os.test;

import com.csu.os.resource.TestPCBManager;

public class TestMessage {
	
	private TestPCBManager tcm = new TestPCBManager();

	public void start() {
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				tcm.init();
				while(tcm.check()>0) {
					
					tcm.getSource();
					
				}
			}
		});
		
		thread.start();
	}
	
	public static void main(String[] args) {
		
		TestMessage testMessage = new TestMessage();
		testMessage.start();
		
	}
}
