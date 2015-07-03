package com.csu.os.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.csu.os.resource.PCB;
import com.csu.os.tools.Parameter;
import com.csu.os.tools.SortById;;

/**
 * PCB类
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class PCBManager implements Runnable {
	
	private int time;//��תʱ��Ƭ
	private int maxReadyNumber;//�����������������������
	private int arithmeticStatus;//��ǰ�����㷨״̬��
	//��Ӧ�����㷨�ļ��
	private enum statusName {
		FCFS,    //�����ȷ�������㷨
		LZ,      //��ת��
		DJFKLZ,  //�༶������ת��
		JTYXJ,   //��̬���ȼ���
		DTYXJ,   //��̬���ȼ���
		ZDZY,    //�����ҵ���ȷ�
		ZGXYB    //�����Ӧ�ȷ�
	}
	private statusName statusCode;//��ǰ�����㷨����
	private List<PCB> totalPCBList;//�����ܼ���
	private List<PCB> initPCBList;//��ʼ״̬���̼���
	private List<PCB> readyPCBList;//����״̬���̼���
	private List<PCB> waitPCBList;//�ȴ�״̬���̼���
	private List<PCB> finishPCBList;//��ɽ��̼���
	private PCB execPCB;//����ִ�н���
	
	/**
	 * �޲ι��췽��
	 */
	public PCBManager() {
		
		totalPCBList = new ArrayList<PCB>();
		initPCBList = new ArrayList<PCB>();
		readyPCBList = new ArrayList<PCB>();
		waitPCBList = new ArrayList<PCB>();
		finishPCBList = new ArrayList<PCB>();
		execPCB = null;
		time = Parameter.TIME_SLICE;
		maxReadyNumber = Parameter.MAX_READY_NUMBER;
		
	}

	/**
	 * ���ι��췽��
	 * @param time ��תʱ��Ƭ��С
	 * @param maxReadyNumber ����������������
	 */
	public PCBManager(int time, int maxReadyNumber) {
		super();
		this.time = time;
		this.maxReadyNumber = maxReadyNumber;
	}

	//getter and setter
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getMaxReadyNumber() {
		return maxReadyNumber;
	}

	public void setMaxReadyNumber(int maxReadyNumber) {
		this.maxReadyNumber = maxReadyNumber;
	}

	public int getArithmeticStatus() {
		return arithmeticStatus;
	}

	public void setArithmeticStatus(int arithmeticStatus) {
		this.arithmeticStatus = arithmeticStatus;
	}

	public List<PCB> getTotalPCBList() {
		return totalPCBList;
	}

	public void setTotalPCBList(List<PCB> totalPCBList) {
		this.totalPCBList = totalPCBList;
	}

	public List<PCB> getInitPCBList() {
		return initPCBList;
	}

	public void setInitPCBList(List<PCB> initPCBList) {
		this.initPCBList = initPCBList;
	}

	public List<PCB> getReadyPCBList() {
		return readyPCBList;
	}

	public void setReadyPCBList(List<PCB> readyPCBList) {
		this.readyPCBList = readyPCBList;
	}

	public List<PCB> getWaitPCBList() {
		return waitPCBList;
	}

	public void setWaitPCBList(List<PCB> waitPCBList) {
		this.waitPCBList = waitPCBList;
	}

	public List<PCB> getFinishPCBList() {
		return finishPCBList;
	}

	public void setFinishPCBList(List<PCB> finishPCBList) {
		this.finishPCBList = finishPCBList;
	}

	public PCB getExecPCB() {
		return execPCB;
	}

	public void setExecPCB(PCB execPCB) {
		this.execPCB = execPCB;
	}
	
	public statusName getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(statusName statusCode) {
		this.statusCode = statusCode;
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arithmeticStatus;
		result = prime * result + ((execPCB == null) ? 0 : execPCB.hashCode());
		result = prime * result + ((finishPCBList == null) ? 0 : finishPCBList.hashCode());
		result = prime * result + ((initPCBList == null) ? 0 : initPCBList.hashCode());
		result = prime * result + maxReadyNumber;
		result = prime * result + ((readyPCBList == null) ? 0 : readyPCBList.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + time;
		result = prime * result + ((totalPCBList == null) ? 0 : totalPCBList.hashCode());
		result = prime * result + ((waitPCBList == null) ? 0 : waitPCBList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PCBManager other = (PCBManager) obj;
		if (arithmeticStatus != other.arithmeticStatus)
			return false;
		if (execPCB == null) {
			if (other.execPCB != null)
				return false;
		} else if (!execPCB.equals(other.execPCB))
			return false;
		if (finishPCBList == null) {
			if (other.finishPCBList != null)
				return false;
		} else if (!finishPCBList.equals(other.finishPCBList))
			return false;
		if (initPCBList == null) {
			if (other.initPCBList != null)
				return false;
		} else if (!initPCBList.equals(other.initPCBList))
			return false;
		if (maxReadyNumber != other.maxReadyNumber)
			return false;
		if (readyPCBList == null) {
			if (other.readyPCBList != null)
				return false;
		} else if (!readyPCBList.equals(other.readyPCBList))
			return false;
		if (statusCode != other.statusCode)
			return false;
		if (time != other.time)
			return false;
		if (totalPCBList == null) {
			if (other.totalPCBList != null)
				return false;
		} else if (!totalPCBList.equals(other.totalPCBList))
			return false;
		if (waitPCBList == null) {
			if (other.waitPCBList != null)
				return false;
		} else if (!waitPCBList.equals(other.waitPCBList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCBManager [time=" + time + ", maxReadyNumber=" + maxReadyNumber + ", arithmeticStatus="
				+ arithmeticStatus + ", statusCode=" + statusCode + ", totalPCBList=" + totalPCBList + ", initPCBList="
				+ initPCBList + ", readyPCBList=" + readyPCBList + ", waitPCBList=" + waitPCBList + ", finishPCBList="
				+ finishPCBList + ", execPCB=" + execPCB + "]";
	}
	
	
	
	/**
	 * ��ӵ���PCB����
	 */
	public void addPCB() {
		
		//�������һ��PCBʵ��
		PCB pcb = new PCB();
		
		//���жϾ��������Ƿ�����
		if(readyPCBList.size() == Parameter.MAX_READY_NUMBER) {
			
			//�����������������򽫽�����ӵ���ʼ��״̬������
			pcb.setStatus(0);
			initPCBList.add(pcb);
		} else {
			
			//���򣬽�������ӵ�����״̬������
			pcb.setStatus(1);
			readyPCBList.add(pcb);
		}
		
		
	}
	
	
	/**
	 * �ж�readyPCBList�����Ƿ�Ϊ�շ���
	 * Ϊ�գ�����true�� ���򷵻�false
	 * @return true|false
	 */
	public boolean isReadyNull() {
		
		if(readyPCBList.size() == 0) {
			return true;
		} else {
			return false;
		}
		
	}

	
	/**
	 * FCFS
	 * �����ȷ�������㷨
	 */
	public void fcfs() {
		
		//���жϵ�ǰ�Ƿ��н�����ִ��
		if(execPCB != null) {
			//�����ǣ����˳����ȵ�ǰ����ִ����
			execPCB.setRunTime(execPCB.getRunTime()-time);
			//�жϵ�ǰ�����Ƿ��Ѿ�ִ�����
			if(execPCB.getRunTime() <= 0) {
				
				//��ִ����ϣ�����ǰ����״̬����Ϊ���״̬
				execPCB.setRunTime(0);
				execPCB.setStatus(4);
				
				//��������ӵ���ɽ��̶�����
				PCB pcb = new PCB(execPCB);
				finishPCBList.add(pcb);
				execPCB = null;
			}
			return;
		}
		
		//���ж�readyPBCList�����Ƿ�Ϊ��
		if(isReadyNull()) {
			//��Ϊ�����˳�
			return;
		}
		
		//����ǰ���������е�һ����������Ϊִ��״̬�����Ӿ����������Ƴ�
		readyPCBList.get(0).setStatus(2);
		execPCB = new PCB(readyPCBList.get(0));
		readyPCBList.remove(0);
		
		//ÿִ��һ�Σ���ȥһ����תʱ��Ƭ
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
		//�жϵ�ǰ�����Ƿ��Ѿ�ִ�����
		if(execPCB.getRunTime() <= 0) {
			
			//��ִ����ϣ�����ǰ����״̬����Ϊ���״̬
			execPCB.setRunTime(0);
			execPCB.setStatus(4);
			
			//��������ӵ���ɽ��̶�����
			PCB pcb = new PCB(execPCB);
			finishPCBList.add(pcb);
			execPCB = null;
		}
		
	}
	
	
	/**
	 * ��ת�����㷨
	 */
	public void lz() {
		//Ϊ�˱�����������жϵ�ǰִ�н����Ƿ�Ϊ��
		if(execPCB != null) {
			//�����ǣ����˳����ȵ�ǰ����ִ����
			return;
		}
		
		//���ж�readyPBCList�����Ƿ�Ϊ��
		if(isReadyNull()) {
			//��Ϊ�����˳�
			return;
		}
		
		//ȡ���������еĵ�һ�����̣�����״̬����Ϊִ��״̬��������Ӿ����������Ƴ�
		readyPCBList.get(0).setStatus(2);
		execPCB = new PCB(readyPCBList.get(0));
		System.out.println(execPCB);
		readyPCBList.remove(0);
		//ÿִ��һ�Σ���ȥһ����Ӧ��ʱ��Ƭ
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
		//�жϵ�ǰ�����Ƿ�ִ�����
		if(execPCB.getRunTime() <= 0) {
			
			//��ִ�����
			if(execPCB.getRunTime() < 0) {
				execPCB.setRunTime(0);
			}
			
			//����ǰִ�н���״̬����Ϊ���״̬�������뵽��ɽ��̶�����
			execPCB.setStatus(4);
			PCB pcb = new PCB(execPCB);
			
			//����ǰ�����ÿ�
			execPCB = null;
			
			//������ɵĽ�����ӵ���ɽ��̶�����
			finishPCBList.add(pcb);
		} else {
			
			PCB pcb = new PCB(execPCB);
			//�����жϾ��������Ƿ�����
			if(readyPCBList.size() == Parameter.MAX_READY_NUMBER) {
				//���ǣ��򽫽��̷����ʼ����
				initPCBList.add(pcb);
			} else {
				//���򣬽����̷����������
				readyPCBList.add(pcb);
			}
			
			//����ǰִ�н����ÿ�
			execPCB = null;
			
		}
		
	}
	
	
	/**
	 * �༶������ת�㷨
	 */
	public void djfklz() {
		
	}
	
	
	/**
	 * ��̬���ȼ��㷨
	 */
	public void jtyx() {
		
	}
	
	
	/**
	 * ��̬���ȼ��㷨
	 */
	public void dtyx() {
		
	}
	
	
	/**
	 * �����ҵ�����㷨
	 */
	public void zdzy() {
		
		//���жϵ�ǰ�Ƿ��н�����ִ��
		if(execPCB != null) {
			//�����ǣ����˳����ȵ�ǰ����ִ����
			execPCB.setRunTime(execPCB.getRunTime()-time);
			//�жϵ�ǰ�����Ƿ��Ѿ�ִ�����
			if(execPCB.getRunTime() <= 0) {
				
				//��ִ����ϣ�����ǰ����״̬����Ϊ���״̬
				execPCB.setRunTime(0);
				execPCB.setStatus(4);
				
				//��������ӵ���ɽ��̶�����
				PCB pcb = new PCB(execPCB);
				finishPCBList.add(pcb);
				execPCB = null;
			}
			return;
		}
		
		//���ж�readyPBCList�����Ƿ�Ϊ��
		if(isReadyNull()) {
			//��Ϊ�����˳�
			return;
		}  else {
			//��readyPCBList�������н�������
			Collections.sort(readyPCBList, new SortById());
		}
		
		//����ǰ���������е�һ����������Ϊִ��״̬�����Ӿ����������Ƴ�
		readyPCBList.get(0).setStatus(2);
		execPCB = new PCB(readyPCBList.get(0));
		readyPCBList.remove(0);
		System.out.println(execPCB);
		
		//ÿִ��һ�Σ���ȥһ����תʱ��Ƭ
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
		//�жϵ�ǰ�����Ƿ��Ѿ�ִ�����
		if(execPCB.getRunTime() <= 0) {
			
			//��ִ����ϣ�����ǰ����״̬����Ϊ���״̬
			execPCB.setRunTime(0);
			execPCB.setStatus(4);
			
			//��������ӵ���ɽ��̶�����
			PCB pcb = new PCB(execPCB);
			finishPCBList.add(pcb);
			execPCB = null;
		}
		
	}
	
	
	/**
	 * �����Ӧ�������㷨
	 */
	public void zgxyb() {
		
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true) {
			
			System.out.println(execPCB);
			System.out.println(initPCBList.size()+"----"+readyPCBList.size()+"----"+
					waitPCBList.size()+"----"+finishPCBList.size());
			
			switch(arithmeticStatus) {
			//ִ�������ȷ�������㷨
			case 0:
				fcfs();
				break;
				
			//ִ����ת�����㷨
			case 1:
				lz();
				break;
				
			//ִ�ж༶������ת�����㷨
			case 2:
				dtyx();
				break;
				
			//ִ�о�̬���ȼ������㷨
			case 3:
				jtyx();
				break;
				
			//ִ�ж�̬���ȼ������㷨
			case 4:
				dtyx();
				break;
				
			//ִ�������ҵ�����㷨
			case 5:
				zdzy();
				break;
				
			//ִ�������Ӧ�����ȵ����㷨
			case 6:
				zgxyb();
				break;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
}
