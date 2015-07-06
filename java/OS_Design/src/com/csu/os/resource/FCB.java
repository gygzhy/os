package com.csu.os.resource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import com.csu.os.resource.Disk.DiskSection;

public class FCB {
	
	// the head of a link-list of disksection
	private Disk.DiskSection head;
	private Disk.DiskSection tail;
	
	private Disk.DiskFragment fragment;
	
	private UUID id;
	
	private String name;
	
	private boolean isFolder;
	
	private FCB parent;
	// store the sub fcb
	private ArrayList<FCB> subFcbs;
	
	public String getName() {
		return name;
	}

	public Disk.DiskFragment getFragment() {
		return fragment;
	}


	public boolean isFolder() {
		return isFolder;
	}



	public FCB getParent() {
		return parent;
	}



	public ArrayList<FCB> getSubFcbs() {
		return subFcbs;
	}



	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		if (isFolder) {
			int size = 0;
			for (FCB fcb : subFcbs) {
				size += fcb.getSize();
			}
			return size;
		} else {
			int size = 0;
			Disk.DiskSection cur = head;
			while (cur != null) {
				size ++;
				cur = cur.next;
			}
			
			return (size - 1) * fragment.disk.sectionSize + tail.data.size(); 
		}
	}
	
	public String getPath() {
		FCB cur = this;
		String path = "";
		while (cur != null) {
			path = cur.name + " / " + path;
			cur = cur.parent;
		}
		return path;
	}

	public UUID getId() {
		return id;
	}
	
	public String getIdString() {
		return id.toString();
	}
	
	/**
	 * delete a file
	 */
	public void delete() {
		if (isFolder) {
			for(int i = 0, l = subFcbs.size(); i < l; i ++) {
				subFcbs.get(0).delete();
			}
			head.free();
		} else {
			Disk.DiskSection cur = head;
			while (cur != null) {
				// set the section to be idle so it's writable
				cur = cur.free();
			}
			
			head = null;
		}
		
		if(parent != null) {
			parent.subFcbs.remove(this);
			parent = null;
		}
		
		fragment = null;
	}
	
	public void addSubFcb(FCB fcb) {
		if (isFolder) {
			fcb.parent = this;
			subFcbs.add(fcb);
			return;
		}
		
		System.err.println("Not a folder");
	}
	
	public FCB createSubFile() {
		if (isFolder) {
			FCB fcb = new FCB(fragment);
			fcb.parent = this;
			subFcbs.add(fcb);
			return fcb;
		}
		
		System.err.println("Not a folder");
		return null;
	}
	
	public FCB createSubFolder() {
		if (isFolder) {
			FCB fcb = new FCB(fragment, true);
			fcb.parent = this;
			subFcbs.add(fcb);
			return fcb;
		}
		System.err.println("Not a folder");
		return null;
	}
	
	public FCB getFcbById(String id) throws Exception {
		if (isFolder) {
			for (FCB fcb : subFcbs) {
				if (fcb.id.toString().equals(id)) {
					return fcb;
				}
			}
			return null;
		} else {
			System.err.println("This is not a folder");
			return null;
		}
	}
	
	public void append(ArrayList<Character> data) {
		if (isFolder) {
			System.err.println("This is a folder");
			return;
		}
		ArrayList<Character> newData = (ArrayList<Character>)read().clone();
		newData.addAll(data);
		
		replace(newData);
	}
	
	public void append(String data) {
		append(stringToArraylist(data));
	}
	
	public void replace(ArrayList<Character> data) {
		if (isFolder) {
			System.err.println("This is a folder");
			return;
		}
		
		int sectionNumToReplace = (int) Math.ceil((double)data.size() / (double)fragment.disk.sectionSize);
		Disk.DiskSection cur = head;
		for(int i =0; i < sectionNumToReplace; i ++) {
			if (cur == null) {
				cur = fragment.allocateSection(this);
				cur.data.clear();
				for(int j = 0; j < fragment.disk.sectionSize && j < (data.size() - i * fragment.disk.sectionSize); j ++ ) {
					cur.data.add(data.get(i* fragment.disk.sectionSize + j)) ;
				}
				
				tail.next = cur;
			} else {
				cur.data.clear();
				for(int j = 0; j < fragment.disk.sectionSize && j < (data.size() - i * fragment.disk.sectionSize); j ++ ) {
					cur.data.add(data.get(i* fragment.disk.sectionSize + j)) ;
				}
			}
			tail = cur;
			cur = cur.next;
		}
		
		cur = tail.next;
		while (cur != null) {
			cur = cur.free();
		}
		tail.next = null;
	}
	public void replace(String data) {
		replace(stringToArraylist(data));
	}
	
	public ArrayList<Character> read() {
		if (isFolder) {
			System.err.println("This is a folder");
			return null;
		}
		ArrayList<Character> ret = new ArrayList<>(); 
		Disk.DiskSection cur = head;
		while (cur != null) {
			for(int i = 0; i < cur.data.size(); i++) {
				ret.add(cur.data.get(i));
			}
			cur = cur.next;
		}
		
		return ret;
	}
	
	public String readString() {
		return arrayListToString(read());
	}
	
	public FCB(Disk.DiskFragment fragment, boolean isFolder) {
		this.fragment = fragment;
		head = tail = fragment.allocateSection(this);
		head.next = null;
		id = UUID.randomUUID();
		
		if (isFolder) {
			name = "new folder";
		} else {
			name = "new file";
		}
		
		subFcbs = new ArrayList<>();
		
		this.isFolder = isFolder;
	}
	
	public FCB(Disk.DiskFragment fragment) {
		this.fragment = fragment;
		head = tail = fragment.allocateSection(this);
		head.next = null;
		id = UUID.randomUUID();
		
		name = "new file";
		
		subFcbs = null;
		
		this.isFolder = false;
	}
	
	private ArrayList<Character> stringToArraylist(String str) {
		ArrayList<Character> ret = new ArrayList<>();
		for(int i = 0; i < str.length(); i++) {
			ret.add(str.charAt(i));
		}
		return ret;
	}
	
	private String arrayListToString(ArrayList<Character> data) {
		String ret = "";
		if (data == null) {
			return ret;
		}
		for(int i = 0 ; i < data.size(); i++) {
			ret += data.get(i);
		}
		return ret;
	}
}
