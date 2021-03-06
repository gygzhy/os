package com.csu.os.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.csu.os.tools.Tools;

/**
 * PCB进程类
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
	private int size;
	private Memory memory;//内存
	private String user; // 用户的名字
	private Message sendMessage = null;//进程要发送的消息
	private List<Message> receiveMessageList = new ArrayList<Message>();//进程接收到的消息队列
	
	/**
	 * 无参构造方法
	 * @throws Exception 
	 */
	public PCB() throws Exception {
		
		pId = UUID.randomUUID();
		uId = UUID.randomUUID();
		name = Tools.getRandomString(8);
		status = 0;
		timeCUP = Tools.getRandomInteger(0, 5);
		timeRAM = Tools.getRandomInteger(0, 5);
		runTime = timeRAM;
		waitTime = 0;
		size = Tools.getRandomInteger(1, 100);
		level = Tools.getRandomInteger(0, 4);
		memory = Memory.Allocate(size, this);
	}
	
	
	public int getSize() {
		return size;
	}


	/**
	 * 带参构造方法
	 * @param pcb 要copy的pcb
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
		this.user = pcb.getUser();
		this.size = pcb.getSize();
		
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
	 * @throws Exception 
	 */
	public PCB(UUID pId, UUID uId, int status, int timeCUP, int timeRAM, int runTime, int waitTime, int level, int size) throws Exception {
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
		this.size = size;
	}
	
	
	
	public PCB(String user, String name, int runTime, int level, int memory) throws Exception {
		super();
		this.memory = Memory.Allocate(memory, this);
		this.user = user;
		this.name = name;
		this.runTime = runTime;
		this.level = level;
		this.waitTime = 0;
		this.pId = UUID.randomUUID();
		this.size = memory;
	}

	public String getUser() {
		return user;
	}

	public UUID getpId() {
		return pId;
	}
	public String getpIdString() {
		if (pId == null)
			return "";
		return pId.toString();
	}
	public void setpId(UUID pId) {
		this.pId = pId;
	}
	public UUID getuId() {
		return uId;
	}
	public String getuIdString() {
		if (uId == null)
			return "";
		return uId.toString();
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

	public Message getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(Message sendMessage) {
		this.sendMessage = sendMessage;
	}


	public List<Message> getReceiveMessageList() {
		return receiveMessageList;
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
		return "PCB [pId=" + pId + ", name=" + name + ", status=" + status + ", runTime=" + runTime + ", level=" + level
				+ ", memory=" + memory + ", user=" + user + "]";
	}

	
	
	
	/**
	 * 进程产生消息方法(消息随机产生)
	 * @param gId 	目标进程id
	 * @param mType	消息类型
	 */
	public void productMessage(UUID gId, int mType) {
		
		Message message = new Message(pId, gId, mType);
		sendMessage = message;
	}
	
	
	/**
	 * 进程产生消息方法(消息数据为手动写入)
	 * @param gId	目标进程id
	 * @param mType	消息类型
	 * @param data	消息数据
	 */
	public void productMessage(UUID gId, int mType, String data) {
		
		Message message = new Message(pId, gId, mType, data);
		sendMessage = message;
	}
	
	
	/**
	 * 输出所有接收到的信息方法
	 * @return 所有接收到的消息数据连接而成的字符串
	 */
	public String outputMessage() {
		
		String allData = "";
		
		//遍历接受信息队列，从中获取信息，并连接起来
		for(Message message:receiveMessageList) {
			
			//连接信息字符串
			allData = allData + message.getmData() + ",";
		}
		
		receiveMessageList.clear();
		return allData;
	}
	
}
