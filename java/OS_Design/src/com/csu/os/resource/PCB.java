package com.csu.os.resource;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

import com.csu.os.tools.Tools;

/**
 * PCB��
 * @author GYGZHY
 * @date   2015��7��3��
 */
public class PCB implements Serializable {
	
	
	private static final long serialVersionUID = 1L;//Ĭ�����л����
	private UUID pId;//����ID
	private UUID uId;//�û�ID
	private String name;//��������
	//���̵�ǰ״̬,0Ϊ��ʼ״̬��1Ϊ����״̬��2Ϊִ��״̬��3Ϊ�ȴ�״̬��4Ϊ����״̬
	private int status;
	private int timeCUP;//CPUռ��ʱ��
	private int timeRAM;//�ڴ�ռ��ʱ��
	private int runTime;//��Ҫִ�е�ʱ��
	private int waitTime;//�ȴ�ʱ��
	private int level;//���ȼ�
	private Memory memory;//�ڴ�ռ�ô�С
	
	/**
	 * �޲ι��췽��
	 * �����������
	 */
	public PCB() {
		
		pId = UUID.randomUUID();
		uId = UUID.randomUUID();
		name = Tools.getRandomString(8);
		status = 0;
		timeCUP = Tools.getRandomInteger(0, 5);
		timeRAM = Tools.getRandomInteger(0, 5);
		runTime = timeRAM;
		waitTime = 0;
		level = Tools.getRandomInteger(0, 4);
		memory = Memory.Allocate(Tools.getRandomInteger(1, 100), this);
	}
	
	/**
	 * ���ι��췽��
	 * @param pcb
	 */
	public PCB(PCB pcb) {
		
		this.pId = pcb.getpId();
		this.uId = pcb.getuId();
		this.name = pcb.getName();
		this.status = pcb.getStatus();
		this.timeCUP = pcb.getTimeCUP();
		this.timeRAM = pcb.getTimeRAM();
		this.waitTime = pcb.getWaitTime();
		this.level = pcb.getLevel();
		this.memory = pcb.getMemory();
		this.runTime = pcb.getRunTime();
		
	}
	
	/**
	 * ���ι��췽��
	 * @param pId
	 * @param uId
	 * @param status
	 * @param timeCUP
	 * @param timeRAM
	 * @param runTime
	 * @param waitTime
	 * @param level
	 * @param size
	 */
	public PCB(UUID pId, UUID uId, int status, int timeCUP, int timeRAM, int runTime, int waitTime, int level, int size) {
		super();
		this.pId = pId;
		this.uId = uId;
		this.status = status;
		this.timeCUP = timeCUP;
		this.timeRAM = timeRAM;
		this.runTime = runTime;
		this.waitTime = waitTime;
		this.level = level;
		this.memory = Memory.Allocate(size, this);
	}
	
	public UUID getpId() {
		return pId;
	}
	public void setpId(UUID pId) {
		this.pId = pId;
	}
	public UUID getuId() {
		return uId;
	}
	public void setuId(UUID uId) {
		this.uId = uId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTimeCUP() {
		return timeCUP;
	}
	public void setTimeCUP(int timeCUP) {
		this.timeCUP = timeCUP;
	}
	public int getTimeRAM() {
		return timeRAM;
	}
	public void setTimeRAM(int timeRAM) {
		this.timeRAM = timeRAM;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public int getRunTime() {
		return runTime;
	}
	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uId == null) ? 0 : uId.hashCode());
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
		PCB other = (PCB) obj;
		if (uId == null) {
			if (other.uId != null)
				return false;
		} else if (!uId.equals(other.uId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCB [pId=" + pId + ", uId=" + uId + ", name=" + name + ", status=" + status + ", timeCUP=" + timeCUP
				+ ", timeRAM=" + timeRAM + ", runTime=" + runTime + ", waitTime=" + waitTime + ", level=" + level
				+ ", memory=" + memory + "]";
	}
	
	
}
