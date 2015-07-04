package com.csu.os.resource;

import java.util.UUID;

import com.csu.os.tools.Tools;

/**
 * 消息类
 * @author GYGZHY
 * @date   2015年7月4日
 */
public class Message {

	private UUID mId;//消息编号
	private UUID sId;//源进程id
	private UUID gId;//目标进程Id
	//消息类型，0为广播信息，1为单播信息
	private int mType;
	private String mData;//数据
	private int mSize;//消息大小
	public UUID getmId() {
		return mId;
	}
	public void setmId(UUID mId) {
		this.mId = mId;
	}
	public UUID getsId() {
		return sId;
	}
	public void setsId(UUID sId) {
		this.sId = sId;
	}
	public UUID getgId() {
		return gId;
	}
	public void setgId(UUID gId) {
		this.gId = gId;
	}
	public int getmType() {
		return mType;
	}
	public void setmType(int mType) {
		this.mType = mType;
	}
	public String getmData() {
		return mData;
	}
	public void setmData(String mData) {
		this.mData = mData;
	}
	public int getmSize() {
		return mSize;
	}
	public void setmSize(int mSize) {
		this.mSize = mSize;
	}
	
	
	/**
	 * 无参构造方法
	 */
	public Message() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 带参构造方法
	 * @param sId 源进程id
	 * @param gId 目标进程id
	 * @param mType 消息类型
	 */
	public Message(UUID sId, UUID gId, int mType) {
		super();
		this.mId = UUID.randomUUID();
		this.sId = sId;
		this.gId = gId;
		this.mType = mType;
		this.mData = Tools.getRandomString(20);
		
		if(mType == 0) {
			gId = null;
		}
		this.mSize = mData.length() + sId.toString().length() + 
				(gId==null?0:gId.toString().length());
		
	}
	/**
	 * 带参构造方法
	 * @param sId 源进程id
	 * @param gId 目标进程id
	 * @param mType 消息类型
	 * @param mData 消息数据
	 */
	public Message(UUID sId, UUID gId, int mType, String mData) {
		super();
		this.mId = UUID.randomUUID();
		this.sId = sId;
		this.gId = gId;
		this.mType = mType;
		this.mData = mData;
		if(mType == 0) {
			gId = null;
		}
		this.mSize = mData.length() + sId.toString().length() + 
				(gId==null?0:gId.toString().length());
	}
	
	
	/**
	 * 带参构造方法（克隆）
	 * @param message
	 */
	public Message(Message message) {
		
		this.mId = message.getmId();
		this.sId = message.getsId();
		this.gId = message.getgId();
		this.mType = message.getmType();
		this.mData = message.getmData();
		this.mSize = message.getmSize();
		
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + ((gId == null) ? 0 : gId.hashCode());
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
		Message other = (Message) obj;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		if (gId == null) {
			if (other.gId != null)
				return false;
		} else if (!gId.equals(other.gId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Message [mId=" + mId + ", sId=" + sId + ", gId=" + gId + ", mType=" + mType + ", mData=" + mData
				+ ", mSize=" + mSize + "]";
	}
	
	
	/**
	 * 输出所有接收到的信息方法
	 * @return 所有接收到的消息数据连接而成的字符串
	 */
	public String outputMessage() {
		
		String allData = "";
		
		return allData;
	}
	
}
