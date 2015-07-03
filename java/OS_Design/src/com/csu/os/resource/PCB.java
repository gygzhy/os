package com.csu.os.resource;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

import com.csu.os.tools.Tools;

/**
 * PCB类
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class PCB implements Serializable {
	
	
	private static final long serialVersionUID = 1L;//默认序列化序号
	private UUID pId;//进程ID
	private UUID uId;//用户ID
	private String name;//进程名称
	//进程当前状态,0为初始状态，1为就绪状态，2为执行状态，3为等待状态，4为结束状态
	private int status;
	private int timeCUP;//CPU占用时间
	private int timeRAM;//内存占用时间
	private int runTime;//需要执行的时间
	private int waitTime;//等待时间
	private int level;//优先级
	private int size;//内存占用大小
	
	/**
	 * 无参构造方法
	 * 属性随机产生
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
		size = Tools.getRandomInteger(1, 100);
	}
	
	/**
	 * 带参构造方法
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
		this.size = pcb.getSize();
		this.runTime = pcb.getRunTime();
		
	}
	
	/**
	 * 带参构造方法
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
		this.size = size;
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
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
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
		result = prime * result + level;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pId == null) ? 0 : pId.hashCode());
		result = prime * result + runTime;
		result = prime * result + size;
		result = prime * result + status;
		result = prime * result + timeCUP;
		result = prime * result + timeRAM;
		result = prime * result + ((uId == null) ? 0 : uId.hashCode());
		result = prime * result + waitTime;
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
		if (level != other.level)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pId == null) {
			if (other.pId != null)
				return false;
		} else if (!pId.equals(other.pId))
			return false;
		if (runTime != other.runTime)
			return false;
		if (size != other.size)
			return false;
		if (status != other.status)
			return false;
		if (timeCUP != other.timeCUP)
			return false;
		if (timeRAM != other.timeRAM)
			return false;
		if (uId == null) {
			if (other.uId != null)
				return false;
		} else if (!uId.equals(other.uId))
			return false;
		if (waitTime != other.waitTime)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCB [pId=" + pId + ", uId=" + uId + ", name=" + name + ", status=" + status + ", timeCUP=" + timeCUP
				+ ", timeRAM=" + timeRAM + ", runTime=" + runTime + ", waitTime=" + waitTime + ", level=" + level
				+ ", size=" + size + "]";
	}

	
}
