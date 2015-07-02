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
		
		currentAllocatePointer = head;
		
		idleSize = totalSize;
	}
	
	public int getSize() {
		return size;
	}

	public String getId() {
		return id.toString();
	}

	// 隐藏构造函数
	private Memory(int size, int floor, int ceil, boolean idle) {
		this.size = size;
		this.id = UUID.randomUUID();
		this.memCeil = ceil;
		this.memFloor = floor;
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
			currentAllocatePointer = mem;
		}
		
		return mem;
	}
	
	/**
	 * 第一个适配 算法
	 * @param size 要分配的内存大小
	 * @return 分配出去的内存
	 */
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
	
	/**
	 * 下一个适配算法
	 * @param size 要分配的内存大小
	 * @return 分配出去的内存
	 */
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
	/**
	 * 最佳适配算法，找到最小的空闲内存
	 * @param size 要分配的内存大小
	 * @return 分配出去的内存
	 */
	static private Memory bestFit(int size) {
		Memory cur = head, ret = null;
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
	
	/**
	 * 最坏适配算法，找到最大的空闲内存块
	 * @param size 要分配的内存大小
	 * @return 分配出去的内存
	 */
	static private Memory worstFit(int size) {
		Memory cur = head, ret = null;
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
		
		Memory first = new Memory(size, mem.memFloor, mem.memFloor + size - 1, false);
		Memory second = new Memory(mem.size - size, first.memCeil + 1,
				mem.memCeil, true);
		
		first.insertAfter(mem.prev);
		second.insertAfter(first);
		
		// preserve the head
		if (mem == head) {
			head = first;
		}
		
		mem.drop();
		
		return first;
	}
	
	/**
	 * 将当前内存与另外一块内存合并生成一个新的内存
	 * @param another 另一块内存
	 * @return 一块新的内存
	 */
	private Memory merge(Memory another) {
		// 避免两个非空闲分区、循环链别的头和尾部节点在中间有其他节点的情况下和两个不相邻的节点合并
		if (!isIdle || !another.isIdle || (next != another && prev != another) ||
				(this == head && another == this.prev && another != this.next) ||
				(another == head && another.prev == this && another.next != this)) {
			return this;
		}
		
		Memory newMem = new Memory(size + another.size, Math.min(memFloor, another.memFloor), Math.max(memCeil, another.memCeil), true);
		newMem.insertAfter(prev);
		
		if (this == head || another == head) {
			head = newMem;
		}
		
		drop();
		another.drop();
		
		return newMem;
	}
	
	/**
	 * 在内存链表中某个位置后面插入一块内存块
	 * @param to 要插入的位置
	 */
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
	 * @return 回收后当前的内存
	 */
	public Memory free() {
		
		if (isIdle) {
			return this;
		}
		
		isIdle = true;
		Memory cur = this;
		if (cur.prev.isIdle && cur.prev != cur) {
			cur = cur.merge(prev);
		}
		
		if (cur.next.isIdle && cur.next != cur) {
			cur = cur.merge(next);
		}
		
		idleSize += size;
		
		return cur;
	}
	
	/**
	 * 回收所有内存 
	 */
	public static void freeAll() {
		Memory cur = head;
		do {
			cur = cur.free();
		} while((cur = cur.next) != head);
	}
	
	static public int getMemoryIdleSectionNum() {
		return getMemorySectionNum(1);
	}
	
	static public int getMemoryBusySectionNum() {
		return getMemorySectionNum(2);
	}
	
	static public int getMemoryAllSectionNum() {
		return getMemorySectionNum(0);
	}
	
	/**
	 * 获取内存的块数
	 * 0 获取所有块，1只获取空闲块，2只获取占用块
	 * @return
	 */
	static private int getMemorySectionNum(int mode) {
		int i = 0;
		Memory cur = Memory.getHead();
		do {
			if (mode == 0 || (mode == 1 && cur.isIdle) || (mode == 2 && !cur.isIdle)) {
				++i;
			}
		} while ((cur = cur.getNext()) != Memory.getHead());
		
		return i;
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
}
