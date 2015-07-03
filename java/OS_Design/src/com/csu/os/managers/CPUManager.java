package com.csu.os.managers;

import com.csu.os.tools.Parameter;

/**
 * CPU管理类
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class CPUManager {

	private PCBManager  pcbManager;
	private Thread thread;
	private int flag = 0;
	
	/**
	 * 无参构造方法
	 */
	public CPUManager() {
		
		pcbManager = new PCBManager();
		
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
					pcbManager.updateTotalPCBList();
					System.out.println(pcbManager.getExecPCB());
					System.out.println("size:"+pcbManager.getTotalPCBList().size());
					for(int i=0; i<10; i++) {
						System.out.println(pcbManager.getTotalPCBList().get(i));
					}
					System.out.println(pcbManager.getInitPCBList().size()+"----"+pcbManager.getReadyPCBList().size()+"----"+
							pcbManager.getWaitPCBList().size()+"----"+pcbManager.getFinishPCBList().size());
					
					switch(pcbManager.getArithmeticStatus()) {
					//ִ�������ȷ�������㷨
					case 0:
						pcbManager.fcfs();
						break;
						
					//ִ����ת�����㷨
					case 1:
						pcbManager.lz();
						break;
						
					//ִ�ж༶������ת�����㷨
					case 2:
						pcbManager.dtyx();
						break;
						
					//ִ�о�̬���ȼ������㷨
					case 3:
						pcbManager.jtyx();
						break;
						
					//ִ�ж�̬���ȼ������㷨
					case 4:
						pcbManager.dtyx();
						break;
						
					//ִ�������ҵ�����㷨
					case 5:
						pcbManager.zdzy();
						break;
						
					//ִ�������Ӧ�����ȵ����㷨
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
