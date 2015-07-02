package com.csu.os.resource;
import org.junit.Test;
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

public class Test_Memory {
	@Test
	public void AllocateMemoryWithNF() {
		Memory mem = Memory.Allocate(300);
		Memory.setAllocateMode(Memory.mode.NF);
		
		Memory mem2 = Memory.Allocate(20);
		Memory mem3 = Memory.Allocate(500);
		
		mem2.free();
		
		Memory mem4 = Memory.Allocate(15);
		
		assertEquals(mem3.getMemCeil() + 1, mem4.getMemFloor());
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
		
		
		assertEquals(Memory.getMemoryAllSectionNum(), 1);
	}
	
	@Test
	public void AllocateMemoryWithBF() {
		Memory.setAllocateMode(Memory.mode.BF);
		Memory mem1 = Memory.Allocate(300);
		Memory mem2 = Memory.Allocate(500);
		Memory mem3 = Memory.Allocate(148);
		Memory mem4 = Memory.Allocate(58);
		
		mem1.free();
		mem3.free();
		
		Memory mem5 = Memory.Allocate(23);
		
		assertEquals(mem2.getMemCeil() + 1, mem5.getMemFloor());
	}
	
	@Test
	public void AllocateMemoryWithWF() {
		Memory.setAllocateMode(Memory.mode.WF);
		Memory mem1 = Memory.Allocate(148);
		Memory mem2 = Memory.Allocate(500);
		Memory mem3 = Memory.Allocate(1024);
		Memory mem4 = Memory.Allocate(58);
		
		mem1.free();
		mem3.free();
		
		Memory mem5 = Memory.Allocate(23);
		
		assertEquals(mem2.getMemCeil() + 1, mem5.getMemFloor());
	}
	
	@Test
	public void MemorySectionNotMergeWhenHeadSectionAndTailSectionIsIdle() {
		Memory mem = Memory.Allocate(500);
		Memory mem2 = Memory.Allocate(200);
		
		assertEquals(Memory.getMemoryAllSectionNum(), 3);
		mem.free();
		assertEquals(Memory.getMemoryAllSectionNum(), 3);
	}
	
	@Test
	public void MemorySectionMergeWhenNotHeadSectionOrTailSectionIsIdle() {
		Memory mem = Memory.Allocate(500);
		Memory mem2 = Memory.Allocate(200);
		Memory mem3 = Memory.Allocate(600);
		
		assertEquals(Memory.getMemoryAllSectionNum(), 4);
		mem3.free();
		assertEquals(Memory.getMemoryAllSectionNum(), 3);
		mem2.free();
		assertEquals(Memory.getMemoryAllSectionNum(), 2);
	}
	
	@Test
	public void MemorySectionNotMergeWhenNeighbourSectionsAreBusy() {
		Memory mem = Memory.Allocate(500);
		Memory mem2 = Memory.Allocate(200);
		Memory mem3 = Memory.Allocate(600);
		
		assertEquals(Memory.getMemoryAllSectionNum(), 4);
		mem2.free();
		assertEquals(Memory.getMemoryAllSectionNum(), 4);
	}
	
	@Test
	public void NullIsReturnedWhenAllocateSizeIsTooLarge() {
		Memory mem = Memory.Allocate(300);
		Memory mem2 = Memory.Allocate(1023);
		Memory mem3 = Memory.Allocate(2123);
		
		assertThat(mem3, new IsNull<Memory>());
	}
	
	@After
	public void tearDown() {
		Memory.freeAll();
	}

}