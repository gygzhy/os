package com.csu.os.resource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.plaf.metal.MetalFileChooserUI.FilterComboBoxRenderer;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.omg.CORBA.PRIVATE_MEMBER;

public class Memory {
	public static enum mode {
		FF, NF, BF, WF, QF
	}
	
	static private final int totalSize = 2048;
	static private final String unit = "byte";
	
	// 分页模式下每个页面的大小
	static private final int pageSize = 16;
	
	// 内存的大小，分配之后的内存是不能改变大小的
	private int size;
	// 内存的标志，分配之后同样不可以更改大小
	private UUID id;
	// 内存的起始位置
	private int memCeil;
	// 内存的结束为止
	private int memFloor;
	
	// 内存链表的下一块内存
	private Memory next;
	private Memory prev;
	// 内存链表的下一块空闲内存
	private Memory nextIdle;
	private Memory prevIdle;
	//是否为空闲块
	private boolean isIdle;
	
	// 当前分配指针，用于next fit 分配方式
	static private Memory currentAllocatePointer;
	// 内存头指针，指向内存链的第一块内存
	static private Memory head;
	
	static {
		head = new Memory(totalSize, 0, totalSize - 1, true);
		head.next = head;
		head.prev = head;
		head.nextIdle = head;
		head.prevIdle = head;
	}
	
	public int getSize() {
		return size;
	}

	public UUID getId() {
		return id;
	}

	// 隐藏构造函数
	private Memory(int size, int head, int tail, boolean idle) {
		this.size = size;
		this.id = UUID.randomUUID();
		this.memCeil = head;
		this.memFloor = tail;
		this.isIdle = idle;
	}
	
	// 设置内存的工作模式
	static private mode allocateMode = mode.FF;
	
	public static mode getAllocateMode() {
		return allocateMode;
	}

	public static void setAllocateMode(mode allocateMode) {
		Memory.allocateMode = allocateMode;
	}

	public static int getTotalSize() {
		return totalSize;
	}

	public static String getUnit() {
		return unit;
	}

	public static int getPagesize() {
		return pageSize;
	}
	
	// 内存分配入口
	static public Memory Allocate(int size) {
		Memory mem = null;
		switch(allocateMode) {
		case FF:
			mem = firstFit(size);
			break;
		case NF:
			mem = nextFit(size);
			break;
		case BF:
			mem = bestFit(size);
			break;
		case WF:
			mem = worstFit(size);
			break;
		case QF:
			mem = null;
			break;
		}
		
		return mem;
	}
	
	static private Memory firstFit(int size) {
		Memory cur = head, ret = null;
		do {
			if (cur.isIdle && cur.size >= size) {
				ret = split(cur, size);
				break;
			}
		} while ((cur = cur.nextIdle) != head);
		return ret;
	}
	
	static private Memory nextFit(int size) {
		Memory cur = currentAllocatePointer, ret = null;
		do {
			if (cur.isIdle && cur.size >= size) {
				ret = split(cur, size);
				break;
			}
		} while ((cur = cur.nextIdle) != head);
		return ret;
	}
	
	static private Memory bestFit(int size) {
		Memory cur = currentAllocatePointer, ret = null;
		do {
			if (cur.isIdle && cur.size >= size) {
				if(ret == null || ret.size > cur.size) {
					ret = cur;
				}
			}
		} while ((cur = cur.nextIdle) != head);
		ret = split(ret, size);
		return ret;
	}
	
	static private Memory worstFit(int size) {
		Memory cur = currentAllocatePointer, ret = null;
		do {
			if (cur.isIdle && cur.size >= size) {
				if(ret == null || ret.size < cur.size) {
					ret = cur;
				}
			}
		} while ((cur = cur.nextIdle) != head);
		ret = split(ret, size);
		return ret;
	}
	
	/**
	 * 拆分一块空闲内存，把拆分的前一半返回，后一半保持空闲状态
	 * @return Memory 拆分后的前一半内存
	 */
	static private Memory split(Memory mem, int size) {
		if (mem.size < size || !mem.isIdle) {
			return null;
		}
		
		if (mem.size == size) {
			mem.prev.nextIdle = mem.prevIdle.nextIdle = mem.nextIdle;
			mem.isIdle = false;
			return mem;
		}
		
		Memory first = mem;
		Memory second = new Memory(mem.size - size, mem.memFloor + 1,
				mem.memFloor + mem.size - size, true);
		
		first.isIdle = false;
		
		second.insertAfter(first);
		
		return first;
	}
	
	static private Memory merge(Memory first, Memory second) {
		if (!first.isIdle || !second.isIdle || first.next != second) {
			return null;
		}
		
		first.next = second.next;
		first.nextIdle = second.nextIdle;
		
		second.next.prev = first;
		second.next.prevIdle = first;
		second.nextIdle.prevIdle = first;
		
		first.size += second.size;
		first.memFloor = second.memFloor;
		
		return first;
	}
	
	private void insertAfter(Memory to) {
		next = to.next;
		nextIdle = to.nextIdle;
		prev = to;
		
		if (to.isIdle) {
			prevIdle = to;
		} else {
			prevIdle = to.prevIdle;
		}
		
		to.next = this;
		if (isIdle) {
			to.nextIdle = this;
		}
	}
	
	/**
	 * 回收内存
	 */
	public void free() {
		Memory cur = this;
		if (prev.isIdle) {
			cur = merge(prev, cur);
		}
		
		if (next.isIdle) {
			cur = merge(cur, next);
		}
		
		isIdle = true;
		
	}
}
