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
	//是否为空闲块
	private boolean isIdle;
	
	// 当前分配指针，用于next fit 分配方式
	static private Memory currentAllocatePointer;
	// 内存头指针，指向内存链的第一块内存
	static private Memory head;
	// 空闲块的总大小
	static private int idleSize;
	
	static {
		head = new Memory(totalSize, 0, totalSize - 1, true);
		head.next = head;
		head.prev = head;
		
		idleSize = totalSize;
	}
	
	public int getSize() {
		return size;
	}

	public String getId() {
		return id.toString();
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
	
	public int getMemCeil() {
		return memCeil;
	}

	public int getMemFloor() {
		return memFloor;
	}

	public Memory getNext() {
		return next;
	}

	public Memory getPrev() {
		return prev;
	}

	public boolean isIdle() {
		return isIdle;
	}

	public static Memory getHead() {
		return head;
	}

	public static int getIdleSize() {
		return idleSize;
	}

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
		
		if (mem != null) {
			idleSize -= mem.size;
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
		} while ((cur = cur.next) != head);
		return ret;
	}
	
	static private Memory nextFit(int size) {
		Memory cur = currentAllocatePointer, ret = null;
		do {
			if (cur.isIdle && cur.size >= size) {
				ret = split(cur, size);
				break;
			}
		} while ((cur = cur.next) != head);
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
		} while ((cur = cur.next) != head);
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
		} while ((cur = cur.next) != head);
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
			mem.isIdle = false;
			return mem;
		}
		
		Memory first = new Memory(size, mem.memCeil, mem.memCeil + size - 1, false);
		Memory second = new Memory(mem.size - size, first.memFloor + 1,
				mem.memFloor, true);
		
		first.insertAfter(mem.prev);
		second.insertAfter(first);
		
		// preserve the head
		if (mem == head) {
			head = first;
		}
		
		mem.drop();
		
		return first;
	}
	
	private Memory merge(Memory another) {
		// 避免两个非空闲分区、循环链别的头和尾部节点在中间有其他节点的情况下和两个不相邻的节点合并
		if (!isIdle || !another.isIdle || (next != another && prev != another) ||
				(this == head && another == this.prev && another != this.next) ||
				(another == head && another.prev == this && another.next != this)) {
			return this;
		}
		
		Memory newMem = new Memory(size + another.size, memCeil, another.memFloor, true);
		newMem.insertAfter(prev);
		
		if (this == head || another == head) {
			head = newMem;
		}
		
		drop();
		another.drop();
		
		return newMem;
	}
	
	private void insertAfter(Memory to) {
		next = to.next;
		prev = to;
		
		to.next = this;
		next.prev = this;
	}
	
	/**
	 * 从链表中除去
	 */
	private void drop() {
		prev.next = next;
		next.prev = prev;
	}
	
	/**
	 * 回收内存
	 */
	public void free() {
		isIdle = true;
		Memory cur = this;
		if (cur.prev.isIdle && cur.prev != cur) {
			cur = cur.merge(prev);
		}
		
		if (cur.next.isIdle && cur.next != cur) {
			cur = cur.merge(next);
		}
		
		idleSize += size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Memory other = (Memory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static void main(String[] argvs) {
		Memory mem = Memory.Allocate(300);
		Memory mem2 = Memory.Allocate(20);
		Memory mem3 = Memory.Allocate(3000);
		
		System.out.println(Memory.getIdleSize());
		mem.free();
		mem2.free();
		System.out.println(Memory.getIdleSize());
	}
}
