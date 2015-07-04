package com.csu.os.resource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import com.csu.os.resource.Disk.DiskSection;

public class FCB {
	
	// the head of a link-list of disksection
	private Disk.DiskSection head;
	private Disk.DiskSection tail;
	
	private Disk disk;
	
	private UUID id;
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		int size = 0;
		Disk.DiskSection cur = head;
		while (cur != null) {
			size ++;
			cur = cur.next;
		}
		
		return (size - 1) * disk.sectionSize + tail.data.size(); 
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
		Disk.DiskSection cur = head;
		while (cur != null) {
			// set the section to be idle so it's writable
			cur.free();
			cur = cur.next;
		}
		
		head = null;
	}
	
	public void append(ArrayList<Character> data) {
		ArrayList<Character> newData = (ArrayList<Character>)read().clone();
		newData.addAll(data);
		
		replace(newData);
	}
	
	public void append(String data) {
		append(stringToArraylist(data));
	}
	
	public void replace(ArrayList<Character> data) {
		int sectionNumToReplace = (int) Math.ceil((double)data.size() / (double)disk.sectionSize);
		Disk.DiskSection cur = head;
		for(int i =0; i < sectionNumToReplace; i ++) {
			
			if (cur == null) {
				cur = disk.allocateSection(this);
				for(int j = 0; j < disk.sectionSize && j < (data.size() - i * disk.sectionSize); j ++ ) {
					cur.data.add(data.get(i* disk.sectionSize + j)) ;
				}
			} else {
				cur.data.clear();
				for(int j = 0; j < disk.sectionSize && j < (data.size() - i * disk.sectionSize); j ++ ) {
					cur.data.add(data.get(i* disk.sectionSize + j)) ;
				}
			}
			tail = cur;
			cur = cur.next;
		}
		
		
		while (cur != null) {
			cur.free();
			cur = cur.next;
		}
		tail.next = null;
	}
	public void replace(String data) {
		replace(stringToArraylist(data));
	}
	
	public ArrayList<Character> read() {
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
	
	public FCB(Disk disk) {
		this.disk = disk;
		head = tail = disk.allocateSection(this);
		head.next = null;
		id = UUID.randomUUID();
		name = "new file";
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
		for(int i = 0 ; i < data.size(); i++) {
			ret += data.get(i);
		}
		return ret;
	}
}
