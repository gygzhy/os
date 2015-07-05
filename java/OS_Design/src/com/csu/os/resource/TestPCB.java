package com.csu.os.resource;

import java.util.ArrayList;
import java.util.UUID;

import com.csu.os.tools.Tools;

public class TestPCB {

	private UUID id;//进程id
	private String name;//进程名称
	private ArrayList<TestPCB> sonPCBs = new ArrayList<TestPCB>();//当前进程从进程列表
	
	//需要的资源
	private ArrayList<String> needList = new ArrayList<String>();
	//拥有的资源
	private ArrayList<String> hasList = new ArrayList<String>();
	
	/**
	 * 无参构造方法
	 */
	public TestPCB() {
		this.id = UUID.randomUUID();
		this.name = Tools.getRandomString(8);	
		for(int i=0; i<Tools.getRandomInteger(1, 5); i++) {
			needList.add(Tools.getRandomString(1));
		}
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<TestPCB> getSonPCBs() {
		return sonPCBs;
	}
	public void setSonPCBs(ArrayList<TestPCB> sonPCBs) {
		this.sonPCBs = sonPCBs;
	}
	public ArrayList<String> getNeedList() {
		return needList;
	}
	public void setNeedList(ArrayList<String> needList) {
		this.needList = needList;
	}
	public ArrayList<String> getHasList() {
		return hasList;
	}
	public void setHasList(ArrayList<String> hasList) {
		this.hasList = hasList;
	}
	
	
	
}
