package com.csu.os.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csu.os.resource.Memory;
import com.csu.os.resource.PCB;
import com.csu.os.tools.Parameter;
import com.csu.os.tools.SortByLevel;
import com.csu.os.tools.SortByName;
import com.csu.os.tools.SortByRunTime;
import com.csu.os.tools.SortByWT;

/**
 * PCB类
 * @author GYGZHY
 * @date   2015年7月3日
 */
public class PCBManager {
	
	private int time;//轮转时间片
	private int maxReadyNumber;//就绪队列中允许的最大进程数
	private int arithmeticStatus;//当前调度算法状态号
	//对应调度算法的简称
	private enum statusName {
		FCFS,    //先来先服务调度算法
		LZ,      //轮转法
		DJFKLZ,  //多级反馈轮转法
		JTYXJ,   //静态优先级法
		DTYXJ,   //动态优先级法
		ZDZY,    //最短作业优先法
		ZGXYB    //最高响应比法
	}
	private statusName statusCode;//当前调度算法代号
	private List<PCB> totalPCBList;//进程总集合
	private List<PCB> initPCBList;//初始状态进程集合
	private List<PCB> readyPCBList;//就绪状态进程集合
	private List<PCB> waitPCBList;//等待状态进程集合
	private List<PCB> finishPCBList;//完成进程集合
	private PCB execPCB;//正在执行进程
	private PCB execPCBFlag;//标记正在执行的进程
	
	/**
	 * 无参构造方法
	 */
	public PCBManager() {
		
		totalPCBList = new ArrayList<PCB>();
		initPCBList = new ArrayList<PCB>();
		readyPCBList = new ArrayList<PCB>();
		waitPCBList = new ArrayList<PCB>();
		finishPCBList = new ArrayList<PCB>();
		execPCB = null;
		execPCBFlag = null;
		time = Parameter.TIME_SLICE;
		maxReadyNumber = Parameter.MAX_READY_NUMBER;
		
	}

	/**
	 * 带参构造方法
	 * @param time 轮转时间片大小
	 * @param maxReadyNumber 最大允许就绪进程数
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
		updateTotalPCBList();
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
	
	public PCB getExecPCBFlag() {
		return execPCBFlag;
	}

	public void setExecPCBFlag(PCB execPCBFlag) {
		this.execPCBFlag = execPCBFlag;
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
	 * 更新总进程集合方法
	 */
	public void updateTotalPCBList() {
		
		//清空
		totalPCBList.clear();
		
		//将初始化队列中的进程添加到总进程中
		if(initPCBList.size() > 0) {
			totalPCBList.addAll(initPCBList);
		}
		
		//将就绪队列中的进程添加到总进程中
		if(readyPCBList.size() > 0) {
			totalPCBList.addAll(readyPCBList);
		}
		
		//将等待队列中的进程添加到总进程中
		if(waitPCBList.size() > 0) {
			totalPCBList.addAll(waitPCBList);
		}
		
		//将完成队列中的进程添加到总进程中
		if(finishPCBList.size() > 0) {
			totalPCBList.addAll(finishPCBList);
		}
		
		//将当前正在执行进程添加到总进程中
		if(execPCB != null) {
			
			totalPCBList.add(execPCB);
		}
		
		Collections.sort(totalPCBList, new SortByName());
	}
	
	
	/**
	 * 添加单个PCB方法
	 */
	public void addPCB() {
		
		//新建一个PCB实例
		PCB pcb = null;
		try {
			pcb = new PCB();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		pcb.productMessage(null, 0);
		//判断就绪队列是否已满
		if(readyPCBList.size() == Parameter.MAX_READY_NUMBER) {
			
			//若是，则将PCB添加到初始队列
			pcb.setStatus(0);
			initPCBList.add(pcb);
		} else {
			
			//否则，将PCB添加到就绪队列
			pcb.setStatus(1);
			readyPCBList.add(pcb);
		}
		
	}
	
	public PCB addPCB(String name, String user, int memory, int level, int cputime) throws Exception {
		
		//新建一个PCB实例 
		PCB pcb = null;
		pcb = new PCB(user, name, cputime, level, memory);
		
		//判断就绪队列是否已满
		if(readyPCBList.size() == Parameter.MAX_READY_NUMBER) {
			
			//若是，则将PCB添加到初始队列
			pcb.setStatus(0);
			initPCBList.add(pcb);
		} else {
			
			//否则，将PCB添加到就绪队列
			pcb.setStatus(1);
			readyPCBList.add(pcb);
		}
		return pcb;
	}
	
	
	/**
	 * 判断就绪队列是否为空
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
	 * 检查时，初始化队列与就绪队列之间进程调整方法
	 */
	public void checkAdjust() {
		
		//判断初始化队列是否为空或就绪队列是否已满
		if(initPCBList.size() == 0 ||
				readyPCBList.size() == Parameter.MAX_READY_NUMBER) {
			
			//若是，则不进行调整
			return;
		} else {
			
			//否则，进行调整,将初始化队列中的第一个进程转移到就绪队列中
			PCB pcb = new PCB(initPCBList.get(0));
			initPCBList.get(0).getMemory().free();
			initPCBList.remove(0);
			
			pcb.setStatus(1);
			readyPCBList.add(pcb);
		}
		
	}
	
	public PCB getPcbById(String id) {
		for (PCB pcb : totalPCBList) {
			if (pcb.getpIdString().equals(id)) {
				return pcb;
			}
		}
		return null;
	}
	
	
	/**
	 * 添加PCB时，初始化队列与就绪队列之间的调整
	 * @param pcb 
	 */
	public void addAdjust(PCB pcb) {
		
		//判断初始化队列是否为空
		if(initPCBList.size() > 0) {
			
			//若不为空，则将解挂的进程添加到初始化队列中
			initPCBList.add(pcb);
		
		//否则，判断就绪队列是否已满
		} else if(readyPCBList.size() == Parameter.MAX_READY_NUMBER) {
			
			//若是，则将解挂的进程添加到初始化队列中
			pcb.setStatus(0);
			initPCBList.add(pcb);
		} else {
			
			//否则，将解挂的进程添加到就绪队列中
			pcb.setStatus(1);
			readyPCBList.add(pcb);
		}
		
	}
	
	
	/**
	 * 挂起进程算法
	 * @param pId 要挂起的进程的id
	 * @return 是否挂起成功
	 */
	public boolean hangPCB(String pId) {
		
		//遍历初始化队列，从中寻找对应要挂起的进程
		for(int i=0; i<initPCBList.size(); i++) {
			
			//判断进程id是否相等
			if(pId.equals(initPCBList.get(i).getpId().toString())) {
				
				//如果相等，则将该进程从初始化队列中移到等待队列中
				PCB pcb = new PCB(initPCBList.get(i));
				initPCBList.remove(i);
				pcb.setStatus(3);
				pcb.getMemory().free();
				waitPCBList.add(pcb);
				return true;
			}
		}
		
		//遍历就绪队列，从中寻找对应要挂起的进程
		for(int i=0; i<readyPCBList.size(); i++) {
			
			//判断进程id是否相等
			if(pId.toString().equals(readyPCBList.get(i).getpId().toString())) {
				
				//如果相等，则将该进程就绪队列中移到等待队列中
				PCB pcb = new PCB(readyPCBList.get(i));
				readyPCBList.remove(i);
				pcb.setStatus(3);
				waitPCBList.add(pcb);
				pcb.getMemory().free();
				checkAdjust();
				return true;
			}
		}
		
		//判断挂起的是否是正在执行进程
		if(pId.toString().equals(execPCB.getpId().toString())) {
			
			//如果是，则将该进程就绪队列中移到等待队列中
			PCB pcb = new PCB(execPCB);
			execPCBFlag = new PCB(execPCB);
			pcb.setStatus(3);
			waitPCBList.add(pcb);
			execPCB.getMemory().free();
			execPCB = null;
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 解挂进程算法
	 * @param pId 要解挂的进程的id
	 * @return 是否解挂成功
	 */
	public boolean activatePCB(String pId) {
		
		//遍历等待队列，从中寻找对应要解挂的进程
		for(int i=0; i<waitPCBList.size(); i++) {
			
			//判断进程id是否相等
			if(pId.equals(waitPCBList.get(i).getpId().toString())) {
				
				//如果相等，则将该进程从初始化队列中移到等待队列中
				PCB pcb = waitPCBList.get(i);
						
				try {
					pcb.setMemory(Memory.Allocate(pcb.getSize()));
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				waitPCBList.remove(i);	
				addAdjust(pcb);
	
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 删除进程算法
	 * @param pId 要删除的进程的id
	 * @return 是否删成功
	 */
	public boolean deletePCB(String pId) {
		
		//遍历初始化队列，从中寻找对应要删除的进程
		for(int i=0; i<initPCBList.size(); i++) {
			
			//判断进程id是否相等
			if(pId.equals(initPCBList.get(i).getpId().toString())) {
				
				//如果相等，则将该进程从初始化队列中删除
				initPCBList.get(i).getMemory().free();
				initPCBList.get(i).setStatus(4);
				finishPCBList.add(initPCBList.get(i));
				initPCBList.remove(i);
				return true;
			}
		}
		
		//遍历就绪队列，从中寻找对应要删除的进程
		for(int i=0; i<readyPCBList.size(); i++) {
			
			//判断进程id是否相等
			if(pId.toString().equals(readyPCBList.get(i).getpId().toString())) {
				
				//如果相等，则将该进程就绪队列中删除
				readyPCBList.get(i).getMemory().free();
				readyPCBList.get(i).setStatus(4);
				finishPCBList.add(readyPCBList.get(i));
				readyPCBList.remove(i);
				checkAdjust();
				return true;
			}
		}
		
		//遍历等待队列，从中寻找对应要删除的进程
		for(int i=0; i<waitPCBList.size(); i++) {
			
			//判断进程id是否相等
			if(pId.toString().equals(waitPCBList.get(i).getpId().toString())) {
				
				//如果相等，则将该进程等待队列中删除
				waitPCBList.get(i).getMemory().free();
				waitPCBList.get(i).setStatus(4);
				finishPCBList.add(waitPCBList.get(i));
				waitPCBList.remove(i);
				return true;
			}
		}
		
		//判断删除的是否是正在执行进程
		if(pId.toString().equals(execPCB.getpId().toString())) {
			
			//如果是，则将该进程删除
			execPCB.getMemory().free();
			execPCB.setStatus(4);
			finishPCBList.add(execPCB);
			execPCB = null;
			return true;
		}
		
//		//遍历总进程队列，从中寻找对应要删除的进程
//		for(int i=0; i<totalPCBList.size(); i++) {
//			
//			//判断进程id是否相等
//			if(pId.toString().equals(totalPCBList.get(i).getpId().toString())) {
//				
//				//如果相等，则将该进程从总进程队列中删除
//				totalPCBList.get(i).getMemory().free();
//				totalPCBList.remove(i);
//				return true;
//			}
//		}
		
		return false;
		
	}
	
	
	/**
	 * 抢占资源前准备方法
	 * @param	当前进程回到就绪队列中的位置
	 * @return	就绪队列是否为空
	 */
	public boolean prepare(int index) {
		
		//先判断当前是否有进程在执行
		if(execPCB != null) {
			//判断当前进程是否已经执行完毕
			if(execPCB.getRunTime() <= 0) {
				
				//若执行完毕，将当前进程状态设置为完成状态
				execPCBFlag = execPCB;
				execPCB.setRunTime(0);
				execPCB.setStatus(4);
				
				//并将其添加到完成进程队列中
				finishPCBList.add(execPCB);
				execPCB.getMemory().free();
				execPCB = null;
			} else {
				
				//若没有执行完，则先将当前执行进程放回就绪队列，后再竞争资源
				execPCB.setStatus(1);
				readyPCBList.add(index, execPCB);
			}
		}
		
		//先判断readyPBCList队列是否为空
		if(isReadyNull()) {
			//若为空则退出
			return true;
		}
		
		return false;
		
	}
	

	
	/**
	 * FCFS
	 * 先来先服务调度算法
	 */
	public void fcfs() {
		
		//先判断就绪队列是否为空
		if (prepare(0)) {
			return;
		}
		
		//将当前就绪队列中第一个进程设置为执行状态，并从就绪队列中移除
		readyPCBList.get(0).setStatus(2);
		execPCB = readyPCBList.get(0);
		readyPCBList.remove(0);
		
		//因为是FCFS调度，当就绪队列有两个或以上空位时，才对初始化队列和就绪队列进行调整
		if(readyPCBList.size() < Parameter.MAX_READY_NUMBER-1) {
			checkAdjust();
		}
		
		//每执行一次，减去一个轮转时间片
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
	}
	
	
	/**
	 * 轮转调度算法
	 */
	public void lz() {
		
		//先判断就绪队列是否为空
		if (prepare(readyPCBList.size())) {
			return;
		}
		
		//将当前就绪队列中第一个进程设置为执行状态，并从就绪队列中移除
		readyPCBList.get(0).setStatus(2);
		execPCB = readyPCBList.get(0);
		readyPCBList.remove(0);
		
		//对初始化队列和就绪队列进行调整
		checkAdjust();
		
		//每执行一次，减去一个轮转时间片
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
	}
	
	
	/**
	 * 多级反馈轮转调度算法
	 */
	public void djfklz() {
		
	}
	
	
	/**
	 * 静态优先调度算法
	 */
	public void jtyx() {
		
		//先判断就绪队列是否为空
		if (prepare(0)) {
			return;
		}
		
		//对就绪队列进行排序
		Collections.sort(readyPCBList, new SortByLevel());
		
		//将当前就绪队列中第一个进程设置为执行状态，并从就绪队列中移除
		readyPCBList.get(0).setStatus(2);
		execPCB = readyPCBList.get(0);
		readyPCBList.remove(0);
		
		//对初始化队列和就绪队列进行调整
		checkAdjust();
		
		//每执行一次，减去一个轮转时间片
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
	}
	
	
	/**
	 * 动态优先调度算法
	 */
	public void dtyx() {
		
		//先判断就绪队列是否为空
		if (prepare(0)) {
			return;
		}
		
		//对就绪队列进行排序
		Collections.sort(readyPCBList, new SortByLevel());
		
		//将当前就绪队列中第一个进程设置为执行状态，并从就绪队列中移除
		readyPCBList.get(0).setStatus(2);
		execPCB = readyPCBList.get(0);
		readyPCBList.remove(0);
		
		//对初始化队列和就绪队列进行调整
		checkAdjust();
		
		//每执行一次，减去一个轮转时间片
		execPCB.setRunTime(execPCB.getRunTime()-time);
		//优先级也相对降低
		execPCB.setLevel(execPCB.getLevel()-Parameter.LEVEL_CHANGE);
		
	}
	
	
	/**
	 * 最短作业优先算法
	 */
	public void zdzy() {
		
		//先判断就绪队列是否为空
		if (prepare(0)) {
			return;
		}
		
		//对就绪队列进行排序
		Collections.sort(readyPCBList, new SortByRunTime());
		
		//将当前就绪队列中第一个进程设置为执行状态，并从就绪队列中移除
		readyPCBList.get(0).setStatus(2);
		execPCB = readyPCBList.get(0);
		readyPCBList.remove(0);
		
		//对初始化队列和就绪队列进行调整
		checkAdjust();
		
		//每执行一次，减去一个轮转时间片
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
	}
	
	
	/**
	 * 最高响应比优先算法
	 */
	public void zgxyb() {
		
		if (prepare(0)) {
			return;
		}
		
		//对就绪队列进行排序
		Collections.sort(readyPCBList, new SortByWT());
		
		//将当前就绪队列中第一个进程设置为执行状态，并从就绪队列中移除
		readyPCBList.get(0).setStatus(2);
		execPCB = readyPCBList.get(0);
		readyPCBList.remove(0);
		
		//对初始化队列和就绪队列进行调整
		checkAdjust();
		
		//每执行一次，减去一个轮转时间片
		execPCB.setRunTime(execPCB.getRunTime()-time);
		
	}
	
	
}
