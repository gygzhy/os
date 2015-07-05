package com.csu.os.resource;

import java.util.ArrayList;
import java.util.UUID;

public class Index {
	private String name;
	private final UUID id;
	private ArrayList<Index> subIndex;
	private ArrayList<FCB> subFcbs;
	private Index parent;
	
	public static final Index root;
	static {
		root = new Index();
		root.name = "root";
	}
	
	public FCB getFileById(String id) {
		for (FCB fcb : subFcbs) {
			if (fcb.getIdString() == id) {
				return fcb;
			}
		}
		FCB found = null;
		for (Index index : subIndex) {
			found = index.getFileById(id);
			if (found != null) {
				return found;
			}
		}
		return found;
	}
	
	public Index getIndexById(String id) {
		Index found = null;
		for (Index index : subIndex) {
			if (index.getIdString() == id) {
				return index;
			}
			found = index.getIndexById(id);
			if (found != null) {
				return found;
			}
		}
		return null;
	}
	
	private Index() {
		name = "new folder";
		id = UUID.randomUUID();
		
		subIndex = new ArrayList<>();
		subFcbs = new ArrayList<>();
		parent = null;
	}
	
	public Index createSubIndex() {
		Index index = new Index();
		subIndex.add(index);
		index.parent = this;
		return index;
	}
	
	public void addFcb(FCB fcb) {
		subFcbs.add(fcb);
	}
	
	public void delete() {
		for (FCB fcb : subFcbs) {
			fcb.delete();
		}
		
		for (Index index : subIndex) {
			index.delete();
		}
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

	public ArrayList<Index> getSubIndex() {
		return subIndex;
	}

	public ArrayList<FCB> getSubFcbs() {
		return subFcbs;
	}
}
