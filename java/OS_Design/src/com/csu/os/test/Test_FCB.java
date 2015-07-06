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

public class Test_FCB {
	private Disk disk;
	private Disk.DiskFragment frag;
	
	@Before
	public void Before() {
		disk = new Disk(2048, 32);
		frag = disk.addFragment(1024, "A");
	}
	
	@Test
	public void fcbCreateFile() {
		FCB fcb = new FCB(frag);
		
		assertEquals(0, fcb.getSize());
		String test = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		fcb.replace(test);
		assertThat(test.length(), equalTo(fcb.getSize()));
		assertEquals(test, fcb.readString());
		assertThat(frag.getIdleSectionNum() + 1, equalTo(frag.getStorage().size() - (int) Math.ceil(test.length() / disk.sectionSize)));
	}
	
	@Test
	public void replaceFCB() {
		FCB fcb = new FCB(frag);
		String test = "slkjf ealkfj alsdkfase lkj jkaef lkja selfa selkfja ef asd fasslkjf ealkfj alsdkfase lkj jkaef lkja selfa selkfja ef asd fasdslkjf ealkfj alsdkfase lkj jkaef lkja selfa selkfja ef asd fasdd";
		fcb.replace(test);
		assertEquals(frag.getStorage().size() - 7, frag.getIdleSectionNum());
		assertEquals(test.length(), fcb.getSize());
		assertEquals(test, fcb.readString());
	}
	
	@Test
	public void fcbAppend() {
		FCB fcb = new FCB(frag);
		fcb.append("fuck");
		fcb.append("aaaaaaaaaaaaaaa");
		assertEquals(19, fcb.getSize());
		assertEquals("fuckaaaaaaaaaaaaaaa", fcb.readString());
	}
	
	@Test
	public void fcbDelete() {
		FCB fcb = new FCB(frag);
		fcb.append("fuck");
		fcb.delete();
		assertEquals(frag.getStorage().size() - 1, frag.getIdleSectionNum());
	}
	
	@Test
	public void folderCanNotAppend() {
		FCB fcb = new FCB(frag, true);
		fcb.append("fuck you bitch");
		assertEquals(0, fcb.getSize());
	}
	
	@Test
	public void folderHasTheSameSizeOfItsFiles() {
		FCB folder = new FCB(frag, true);
		FCB file = new FCB(frag);
		FCB file2 = new FCB(frag);
		
		file.append("fuck you bitch ~");
		file2.append("fuck you bitch ~fuck you bitch ~fuck you bitch ~");
		folder.addSubFcb(file);
		folder.addSubFcb(file2);
		
		assertEquals(folder.getSize(), file.getSize() + file2.getSize());
	}
	
	@Test
	public void noDiskTakenAfterDeleteFolderWithSubFolder() {
		FCB folder = new FCB(frag, true);
		FCB folder2 = new FCB(frag, true);
		FCB file = new FCB(frag);
		FCB file2 = new FCB(frag);
		
		file.append("fuck you bitch ~");
		file2.append("fuck you bitch ~fuck you bitch ~fuck you bitch ~");
		folder.addSubFcb(file);
		folder2.addSubFcb(file2);
		folder.addSubFcb(folder2);
		
		folder.delete();
		
		assertEquals(frag.getIdleSectionNum(), frag.getStorage().size() - 1);
	}
	
	@Test
	public void canNotAcessFileAfterDelete() throws Exception {
		FCB folder = new FCB(frag, true);
		FCB folder2 = new FCB(frag, true);
		FCB file = new FCB(frag);
		FCB file2 = new FCB(frag);
		
		file.append("fuck you bitch ~");
		file2.append("fuck you bitch ~fuck you bitch ~fuck you bitch ~");
		folder.addSubFcb(file);
		folder2.addSubFcb(file2);
		folder.addSubFcb(folder2);
		
		folder2.delete();
		
		assertEquals(folder.getFcbById(folder2.getIdString()), null);
		assertEquals(file2.readString(), "");
	}
	
	@Test
	public void canAccessSubFolder() throws Exception {
		FCB folder = new FCB(frag, true);
		FCB file = new FCB(frag);
		
		file.append("fuck you bitch ~");
		folder.addSubFcb(file);
		
		assertEquals(folder.getFcbById(file.getIdString()), file);
	}
	
	@Test
	public void createSubFolder() {
		FCB folder = new FCB(frag, true);
		
		FCB sub = folder.createSubFolder();
		
		FCB file = sub.createSubFile();
		file.append("fuck you");
		
		assertEquals(file.readString(), "fuck you");
	}
}
