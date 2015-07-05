package com.csu.os.managers;

import com.csu.os.tools.Parameter;

/**
 * CPU管理类
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class CPUManager {

	private PCBManager  pcbManager;
	private MessagesManager messagesManager;
	private Thread thread;
	private int flag = 0;
	
	/**
	 * 无参构造方法
	 */
	public CPUManager() {
		
		pcbManager = new PCBManager();
		messagesManager = new MessagesManager();
	}
	
	public void start() {
		
		if (flag != 0) {
			return;
		}
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(true) {
					//pcbManager.updateTotalPCBList();
					System.out.println(pcbManager.getExecPCB());
					System.out.println("size:"+pcbManager.getTotalPCBList().size());
					for(int i=0; i<pcbManager.getTotalPCBList().size(); i++) {
						System.out.println(pcbManager.getTotalPCBList().get(i));
					}
					for(int i=0; i<pcbManager.getTotalPCBList().size(); i++) {
						for(int j=0; j<pcbManager.getTotalPCBList().get(i).getReceiveMessageList().size(); j++) {
							System.out.println(pcbManager.getTotalPCBList().get(i).getReceiveMessageList().get(j));
						}
					}
					System.out.println(pcbManager.getInitPCBList().size()+"----"+pcbManager.getReadyPCBList().size()+"----"+
							pcbManager.getWaitPCBList().size()+"----"+pcbManager.getFinishPCBList().size());
					
					messagesManager.receiveMessage(pcbManager);
					messagesManager.sendMessage(pcbManager);
					
					switch(pcbManager.getArithmeticStatus()) {
					
					//先来先服务调度算法
					case 0:
						pcbManager.fcfs();
						break;
						
					//轮转调度算法
					case 1:
						pcbManager.lz();
						break;
						
					//多级反馈轮转调度算法
					case 2:
						pcbManager.djfklz();
						break;
						
					//静态优先调度算法
					case 3:
						pcbManager.jtyx();
						break;
						
					//动态优先调度算法
					case 4:
						pcbManager.dtyx();
						break;
						
					//最短作业调度算法
					case 5:
						pcbManager.zdzy();
						break;
						
					//最高响应比调度算法
					case 6:
						pcbManager.zgxyb();
						break;
					}
					
					try {
						Thread.sleep(Parameter.SLEEP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		});
		flag = 1;
		thread.start();
	}
	
	public void stop() {
		
		if (flag == 0) {
			return;
		}
		thread.stop();
		flag = 0;
	}

	public PCBManager getPcbManager() {
		return pcbManager;
	}
}
