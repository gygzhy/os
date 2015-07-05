package com.csu.os.managers;

import java.util.ArrayList;
import java.util.List;

import com.csu.os.resource.Message;
import com.csu.os.resource.PCB;
import com.csu.os.tools.Parameter;

/**
 * 消息管理类
 * @author GYGZHY
 * @date   2015年7月4日
 */
public class MessagesManager {

	private List<Message> messageList = new ArrayList<Message>();//消息队列
	private int totalSize = 0;//消息总大小	
	
	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public List<Message> getMessageList() {
		return messageList;
	}

	
	
	/**
	 * 计算消息总大小方法
	 */
	public void calculate() {
		
		//遍历消息队列
		for(Message message:messageList) {
			
			totalSize += message.getmSize();
		}
		
	}
	
	
	
	/**
	 * 收集进程消息方法
	 * @param pcbMessage
	 */
	public void receiveMessage(PCBManager pcbManager) {
		
		//遍历所有进程
		for(PCB pcb:pcbManager.getTotalPCBList()) {
			
			//判断消息管理器队列是否已满
			if(messageList.size() == Parameter.MAX_MESSAGE_NUMBER) {
				break;
			}
			
			//判断是否是已经结束的进程
			if(pcb.getStatus() == 4) {
				continue;
			}
			
			//判断进程是否发送了信息
			if(pcb.getSendMessage() == null) {
				continue;
			}
			
			//否则，将进程要发送的消息放到消息管理器中
			messageList.add(pcb.getSendMessage());
			pcb.setSendMessage(null);
		}
	}
	
	
	/**
	 * 发送消息方法
	 * @param pcbManager
	 */
	public void sendMessage(PCBManager pcbManager) {
		
		//先判断消息队列是否为空
		if(messageList.size() == 0) {
			return;
		}
		
		//否则，进行发送消息操作
		for(Message message:messageList) {
			
			//先判断消息的类型
			if(message.getmType() == 0) {
				
				//如果是广播类型，则将该消息广播给所有除源进程以外的进程
				for(PCB pcb:pcbManager.getTotalPCBList()) {
					
					//判断进程是否为已完成进程
					if(pcb.getStatus() == 4) {
						continue;
					}
					
					Message e = new Message(message);
					pcb.getReceiveMessageList().add(e);
				}
			} else if(message.getmType() == 1) {
				
				//如果是单播类型，则先判断目的进程id是否为空
				if(message.getgId() == null) {
					continue;
				} else {
					
					//遍历所有进程，找出目标进程地址
					for(PCB pcb:pcbManager.getTotalPCBList()) {
						
						//判断进程是否为已完成进程
						if(pcb.getStatus() == 4) {
							continue;
						}
						
						if(pcb.getpId().toString().equals(message.getgId().toString())) {
							
							//若找到目标进程，则将消息发送给目标进程
							pcb.getReceiveMessageList().add(message);
						}
					}
				}
			}
		}
		
	}
	
	
}
