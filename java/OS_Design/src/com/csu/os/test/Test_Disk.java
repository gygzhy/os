package com.csu.os.test;
import java.util.ArrayList;

import com.csu.os.resource.Disk;
import com.csu.os.resource.FCB;


import com.csu.os.resource.Memory;
import com.csu.os.resource.Memory.mode;

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

public class Test_Disk {
	
	private Disk disk;
	
	@Before
	public void Before() {
		disk = new Disk(2048, 32);
	}
	
	@Test
	public void diskHasNoFragment() {
		assertEquals(disk.getAvailFragments().size(), 0);
	}
	
	@Test
	public void diskHasOneFragment() {
		Disk.DiskFragment A = disk.addFragment(1024, "A");
		assertEquals(disk.getAvailFragments().size(), 1);
		assertEquals(disk.getAvailFragments().get(0).getName(), "A");
		assertEquals(disk.getFramentByName("A"), A);
	}
	
	@Test
	public void diskHasTwoFragment() {
		Disk.DiskFragment A = disk.addFragment(1024, "A");
		Disk.DiskFragment B = disk.addFragment(1024, "B");
		
		assertEquals(disk.getAvailFragments().size(), 2);
		assertEquals(disk.getAvailFragments().get(1).getName(), "B");
		assertEquals(disk.getFramentByName("B"), B);
	}
	
	
	@Test
	public void fragmentSize() {
		
	}
}
