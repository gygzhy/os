package com.csu.os.resource;

import java.util.ArrayList;

public class Disk {
	
	// disk storage size
	public final int sectionNum;
	
	// the section size
	public final int sectionSize;
	
	protected ArrayList<DiskSection> storage;
	
	public Disk(int storageSize, int sectionSize) {
		this.sectionNum = storageSize;
		this.sectionSize = sectionSize;
		storage = new ArrayList<>();
		for(int i = 0; i < storageSize; i ++) {
			storage.add(new DiskSection(this));
		}
	}
	
	protected DiskSection allocateSection(FCB fcb) {
		for (DiskSection diskSection : storage) {
			if (diskSection.isIdle) {
				diskSection.isIdle = false;
				diskSection.fcb = fcb;
				return diskSection;
			}
		}
		return null;
	}
	
	public int getIdleSectionNum() {
		int num = 0;
		for (DiskSection diskSection : storage) {
			if (diskSection.isIdle) {
				num ++;
			}
		}
		return num;
	}
	
	public class DiskSection {
		public DiskSection(Disk disk) {
			super();
			next = prev = null;
			data = new ArrayList<Character>();
			isIdle = true;
			this.disk = disk;
			this.fcb = null;
		}
		protected FCB fcb;
		protected ArrayList<Character> data;
		protected DiskSection next;
		protected DiskSection prev;
		protected boolean isIdle;
		protected Disk disk;
		
		
		public ArrayList<Character> getData() {
			return data;
		}
		public void setData(ArrayList<Character> data) {
			this.data = data;
		}
		
		public Disk getDisk() {
			return disk;
		}
		public DiskSection getNext() {
			return next;
		}
		public DiskSection getPrev() {
			return prev;
		}
		public boolean isIdle() {
			return isIdle;
		}
	}
}

