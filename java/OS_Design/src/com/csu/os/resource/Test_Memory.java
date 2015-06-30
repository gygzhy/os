package com.csu.os.resource;
import org.junit.Test;
import junit.framework.TestCase;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test_Memory {
	@Test
	public void AllocateMemoryWithNF() {
		Memory mem = Memory.Allocate(300);
		Memory.setAllocateMode(Memory.mode.NF);
		
		Memory mem2 = Memory.Allocate(20);
		Memory mem3 = Memory.Allocate(500);
		
		mem2.free();
		
		Memory mem4 = Memory.Allocate(15);
		
		assertEquals(mem4.getMemCeil(), mem3.getMemFloor() + 1);
	}
	
	@Test
	public void AllocateMemoryWithFF() {
		Memory mem = Memory.Allocate(300);
		assertEquals(mem.getSize(), 300);
		assertEquals(2048 - 300, Memory.getIdleSize());
	}
	
	@Test
	public void MemoryHaveOneSectionAfterFreeingAll() {
		Memory mem = Memory.Allocate(200);
		Memory mem2 = Memory.Allocate(500);
		Memory mem3 = Memory.Allocate(20);
		mem2.free();
		mem2 = Memory.Allocate(200);
		Memory mem4 = Memory.Allocate(15);
		
		mem.free();
		mem2.free();
		mem3.free();
		mem4.free();
		
		int i = getMemorySectionNum();
		
		assertEquals(i, 1);
	}
	
	@Test
	public void MemorySectionDecreseAfterFreeing() {
		Memory mem = Memory.Allocate(500);
		Memory mem2 = Memory.Allocate(200);
		
		assertEquals(getMemorySectionNum(), 3);
		mem.free();
		assertEquals(getMemorySectionNum(), 3);
		mem2.free();
		assertEquals(getMemorySectionNum(), 1);
	}
	
	private int getMemorySectionNum() {
		int i = 0;
		Memory cur = Memory.getHead();
		do {
			++i;
		} while ((cur = cur.getNext()) != Memory.getHead());
		
		return i;
	}
	
	@After
	public void tearDown() {
		Memory.freeAll();
	}

}
