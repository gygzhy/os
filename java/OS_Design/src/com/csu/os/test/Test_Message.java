package com.csu.os.test;
import java.util.ArrayList;

import com.csu.os.hardware.CPU;
import com.csu.os.managers.CPUManager;
import com.csu.os.managers.PCBManager;
import com.csu.os.resource.Disk;
import com.csu.os.resource.FCB;


import com.csu.os.resource.Memory;
import com.csu.os.resource.Memory.mode;
import com.csu.os.resource.Message;
import com.csu.os.resource.PCB;

import junit.extensions.TestSetup;
import junit.framework.TestCase;
import static org.junit.Assert.assertThat;

import java.lang.management.MemoryType;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;

import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class Test_Message {
	
	private PCB A;
	private PCB B;
	private PCB C;
	
	private static CPUManager cpu;
	
	@BeforeClass
	public static void Before() throws Exception {
		
		cpu = new CPUManager();
		cpu.start();
	}
	
	@Before
	public void before() throws Exception {
		A = cpu.getPcbManager().addPCB("A", null, 200, 30, 43);
		B = cpu.getPcbManager().addPCB("B", null, 200, 30, 43);
		C = cpu.getPcbManager().addPCB("C", null, 200, 30, 43);
	}
	
	@Test
	public void bRecieveMessageAfterABCast() throws InterruptedException {
		A.productMessage(null, 0, "a hey");
		Thread.sleep(5000);
		assertEquals("a hey,", B.outputMessage());
	}
	
	@Test
	public void multipleProcessBCast() throws InterruptedException {
		A.productMessage(null, 0, "a hey hey");
		B.productMessage(null, 0, "b hey hey");
		
		Thread.sleep(1200);
		
		assertEquals("a hey hey,b hey hey,", C.outputMessage());
		assertEquals("b hey hey,", A.outputMessage());
		assertEquals("a hey hey,", B.outputMessage());
	}
	
	@Test
	public void aRecieveMessageAfterASingleCast() throws InterruptedException {
		A.productMessage(B.getpId(), 1, "a hey");
		Thread.sleep(1200);
		
		assertEquals("a hey,", B.outputMessage());
	}
	
	@Test
	public void aRecieveMessageAfterAAndCSingleCast() throws InterruptedException {
		A.productMessage(B.getpId(), 1, "a hey");
		C.productMessage(B.getpId(), 1, "c hey");
		Thread.sleep(1200);
		
		assertEquals("a hey,c hey,", B.outputMessage());
	}
	
	@After
	public void after() {
		cpu.getPcbManager().deletePCB(A.getpIdString());
		cpu.getPcbManager().deletePCB(B.getpIdString());
		cpu.getPcbManager().deletePCB(C.getpIdString());
	}

}
