package com.csu.os.utility;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;


public class Logger {
	// a label for classification
	private String label;
	// container for Logs
	private ArrayList<Log> storage;
	
	private int lastQueringIndex;
	
	static private int BufferLength = 300;
	
	public String getLabel() {
		return label;
	}
	

	public Logger(String label) {
		super();
		this.label = label;
		storage = new ArrayList<>();
		lastQueringIndex = 0;
	}
	
	public Log log(String msg) {
		return _log(msg);
	}
	
	public Log log(String format, Object... data) {
		Formatter formatter = new Formatter();
		String str = formatter.format(format, data).toString();
		return _log(str);
	}
	
	private Log _log(String msg) {
		Log log = new Log(msg, Calendar.getInstance().getTimeInMillis(), label);
		if (storage.size() >= BufferLength) {
			storage.remove(0);
		}
		storage.add(log);
		return log;
	}
	
	/**
	 * 获取某个区间的所有log
	 * @param start 开始时间, 设为小于等于0时则从最早的log开始
	 * @param end	结束时间, 设为小于等于0时则查询到最新的log
	 * @return		Log的数组
	 */
	public Log[] getRange(long start, long end) {
		ArrayList<Log> ret = new ArrayList<Logger.Log>(); 
		int found = -1, i;
		for(i = 0; i < storage.size(); i++) {
			if (found == -1 && storage.get(i).time >= start) {
				found = i;
			}
			
			if (end > 0 && storage.get(i).time > end) {
				break;
			}
			
			if (found != -1) {
				ret.add(storage.get(i));
			}
		}
		lastQueringIndex = i;
		return (Log[]) ret.toArray(new Log[ret.size()]);
	}
	
	/**
	 * 获取距离上次查询最新添加的Log
	 * @return	Log数组
	 */
	public Log[] getLatest() {
		return getRange(lastQueringIndex, -1);
	}

	public class Log {
		
		public Log(String msg, long time, String label) {
			super();
			this.msg = msg;
			this.time = time;
			this.label = label;
		}
		
		private String msg;
		private long time;
		// label same as the logger
		private String label;
		
		public String getMsg() {
			return msg;
		}
		public long getTime() {
			return time;
		}
		public String getLabel() {
			return label;
		}
	}
}
