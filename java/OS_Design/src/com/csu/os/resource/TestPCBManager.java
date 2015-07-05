package com.csu.os.resource;

import java.util.ArrayList;

import com.csu.os.tools.Tools;

public class TestPCBManager {
	
	private ArrayList<TestPCB> list = new ArrayList<TestPCB>();
	private ArrayList<String> srcList = new ArrayList<String>();
	
	
	/**
	 * 初始化方法
	 */
	public void init() {
		
		for(int i=0; i<5; i++) {
			TestPCB testPCB = new TestPCB();
			list.add(testPCB);
			srcList.addAll(testPCB.getNeedList());
			System.out.println("第"+i+"个进程的名称为：" + testPCB.getName());
		}
		
		//将0号进程设置为其他进程的主进程
		for(int i=1; i<5; i++) {
			list.get(0).getSonPCBs().add(list.get(i));
		}
		
	}
	
	/**
	 * 争抢临界资源
	 */
	public void getSource() {
		
		//随机产生一个临界资源
		String src = "a";
		
		if(list.size() == 0) {
			return;
		}
		int index;
		if(srcList.size() > 0) {
			src = srcList.get(Tools.getRandomInteger(0, srcList.size()));
		}
		if(list.size() > 1) {
			index = Tools.getRandomInteger(0, list.size()-1);
		} else {
			index = 0;
		}
		System.out.println("进程"+list.get(index).getName()+"获得时间片：");

		for(int i=0; i<list.get(index).getNeedList().size(); i++) {
			if(src.equals(list.get(index).getNeedList().get(i))) {
				
				if(index == 0) {
					System.out.println("主进程"+list.get(index).getName()+"获得临界资源");
					list.get(index).getNeedList().remove(i);
				} else {
					System.out.println("从进程"+list.get(index).getName()+"获得临界资源");
					for(int j=0; j<list.get(0).getNeedList().size(); j++) {
						if(src.equals(list.get(index).getNeedList().get(j))) {
							System.out.println("主进程"+list.get(0).getName()+"获得子进程资源");
							list.get(0).getNeedList().remove(j);
							break;
						}
					}
				}
				break;
			}
		}
		
	}
	
	/**
	 * 检查进程是否已经完成
	 */
	public int check() {
		srcList.clear();
		for(int i=0; i<list.size(); i++) {
			
			if(list.get(i).getNeedList().size() == 0) {
				System.out.println("进程"+list.get(i).getName()+"完成");
				list.remove(i);
			} else {
				
				srcList.addAll(list.get(i).getNeedList());
			}
			
		}
		
		return list.size();
	}
}
