/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu.data;

/**
 * Holds parsed temporal data from /proc/stat file.
 */
public class CpuCoreTimes {
	private long user = 0;       //  Time spent with normal processing in user mode.
	private long nice = 0;       //  Time spent with niced processes in user mode.
	private long system = 0;     //  Time spent running in kernel mode.
	private long idle = 0;       //  Time spent in vacations twiddling thumbs.
	private long iowait = 0;     //  Time spent waiting for I/O to completed. This is considered idle time too.
	private long irq = 0;        //  Time spent serving hardware interrupts.
	private long softirq = 0;    //  Time spent serving software interrupts.
	private long steal = 0;      //  Time stolen by other operating systems running in a virtual environment.
	private long guest = 0;      //  Time spent for running a virtual CPU or guest OS under the control of the kernel.

	//	The time is measured in USER_HZ (also called Jiffies) which are
	//	typically 1/100ths of a second. USER_HZ is a compile time constant which
	//	can be queried using:
	//      * Shell: getconf CLK_TCK

	//  TODO: Get CLK_TCK value at runtime.


	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public long getNice() {
		return nice;
	}

	public void setNice(long nice) {
		this.nice = nice;
	}

	public long getSystem() {
		return system;
	}

	public void setSystem(long system) {
		this.system = system;
	}

	public long getIdle() {
		return idle;
	}

	public void setIdle(long idle) {
		this.idle = idle;
	}

	public long getIowait() {
		return iowait;
	}

	public void setIowait(long iowait) {
		this.iowait = iowait;
	}

	public long getIrq() {
		return irq;
	}

	public void setIrq(long irq) {
		this.irq = irq;
	}

	public long getSoftirq() {
		return softirq;
	}

	public void setSoftirq(long softirq) {
		this.softirq = softirq;
	}

	public long getSteal() {
		return steal;
	}

	public void setSteal(long steal) {
		this.steal = steal;
	}

	public long getGuest() {
		return guest;
	}

	public void setGuest(long guest) {
		this.guest = guest;
	}
}
