package com.csu.os.resource;

import java.util.ArrayList;
import java.util.UUID;

public class Disk {
	
	// disk storage size
	public final int sectionSize;
	
	public final int sectionNum;
	
	private UUID id;
	
	
	public UUID getId() {
		return id;
	}

	protected ArrayList<DiskFragment> fragments;
	protected ArrayList<DiskSection> sections;
	
	public ArrayList<DiskFragment> getFragments() {
		return fragments;
	}
	
	public ArrayList<DiskFragment> getAvailFragments() {
		ArrayList<DiskFragment> ret = new ArrayList<>();
		for (DiskFragment diskFragment : fragments) {
			if (!diskFragment.isIdle) {
				ret.add(diskFragment);
			}
		}
		return ret;
	}
	
	public ArrayList<DiskSection> getSections() {
		return sections;
	}



	public DiskFragment getFramentByName(String name) {
		for (DiskFragment diskFragment : fragments) {
			if (diskFragment.name.equals(name)) {
				return diskFragment;
			}
		}
		return null;
	}

	public Disk(int sectionNum, int sectionSize) {
		this.sectionSize = sectionSize;
		this.sectionNum = sectionNum;
		
		fragments = new ArrayList<>();
		sections = new ArrayList<>();
		
		DiskFragment idleFragment = new DiskFragment(this);
		
		for(int i = 0; i < sectionNum; i ++) {
			DiskSection sec = new DiskSection(this);
			sections.add(sec);
			idleFragment.storage.add(sec);
		}
		
		fragments.add(idleFragment);
	}
	
	public DiskFragment addFragment(int size, String name) {
		int j = 0;
		for (DiskFragment diskFragment : fragments) {
			if (diskFragment.isIdle && diskFragment.storage.size() > size) {
				DiskFragment ret = new DiskFragment(this);
				for(int i = 0; i < size; i ++) {
					ret.storage.add(diskFragment.storage.get(0));
					diskFragment.storage.remove(0);
				}
				ret.name = name;
				ret.isIdle = false;
				
				fragments.add(j, ret);
				
				return ret;
			} else if (diskFragment.isIdle && diskFragment.storage.size() == size) {
				diskFragment.isIdle = false;
				diskFragment.name = name;
				return diskFragment;
			}
			 ++j;
		}
		return null;
	}

	
	public class DiskFragment {
		
		protected Disk disk;
		
		private boolean isIdle;
		
		private UUID id;
		private String name;
		private ArrayList<DiskSection> storage;
		
		protected DiskSection allocateSection(FCB fcb) {
			for (DiskSection diskSection : storage) {
				if (diskSection.isIdle) {
					diskSection.data.clear();
					diskSection.isIdle = false;
					diskSection.fcb = fcb;
					diskSection.next = null;
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
		
		public DiskFragment(Disk disk) {
			name = "new fragment";
			id = UUID.randomUUID();
			storage = new ArrayList<>();
			this.disk = disk;
			
			isIdle = true;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public UUID getId() {
			return id;
		}
		
		public String getIdString() {
			return id.toString();
		}

		public ArrayList<DiskSection> getStorage() {
			return storage;
		}
		
		public int getSize() {
			return storage.size();
		}

		public boolean isIdle() {
			return isIdle;
		}
		
		
		
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
		
		private UUID id;
		
		
		public UUID getId() {
			return id;
		}
		public ArrayList<Character> getData() {
			return data;
		}
		public void setData(ArrayList<Character> data) {
			this.data = data;
		}
		
		public DiskSection free() {
			isIdle = true;
			data.clear();
			fcb = null;
			DiskSection temp = next;
			next = null;
			return temp;
		}
		
		public FCB getFcb() {
			return fcb;
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

