package com.csu.os.test;
import java.util.ArrayList;

import com.csu.os.resource.Disk;
import com.csu.os.resource.FCB;


import com.csu.os.resource.Memory;
import com.csu.os.resource.Memory.mode;

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

public class Test_FCB {
	private Disk disk = new Disk(2048, 32);
	
	@Test
	public void fcbCreateFile() {
		FCB fcb = new FCB(disk);
		
		assertEquals(0, fcb.getSize());
		fcb.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		assertThat(32, equalTo(fcb.getSize()));
		assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", fcb.readString());
		assertThat(disk.sectionNum - 1, equalTo(disk.getIdleSectionNum()));
	}
	
	@Test
	public void fcbAppend() {
		FCB fcb = new FCB(disk);
		fcb.append("fuck");
		fcb.append("aaaaaaaaaaaaaaa");
		assertEquals(19, fcb.getSize());
		assertEquals("fuckaaaaaaaaaaaaaaa", fcb.readString());
	}
	
	@Test
	public void fcbDelete() {
		FCB fcb = new FCB(disk);
		fcb.append("fuck");
		fcb.delete();
		assertEquals(disk.sectionNum, disk.getIdleSectionNum());
	}
}