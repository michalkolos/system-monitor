/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu.data;

import java.util.Optional;

/**
 * Holds parsed temporal data from /proc/stat file.
 */
public class CpuCoreTimes {
	private Long user = 0L;       //  Time spent with normal processing in user mode.
	private Long nice = 0L;       //  Time spent with niced processes in user mode.
	private Long system = 0L;     //  Time spent running in kernel mode.
	private Long idle = 0L;       //  Time spent in vacations twiddling thumbs.
	private Long iowait = 0L;     //  Time spent waiting for I/O to completed. This is considered idle time too.
	private Long irq = 0L;        //  Time spent serving hardware interrupts.
	private Long softirq = 0L;    //  Time spent serving software interrupts.
	private Long steal = 0L;      //  Time stolen by other operating systems running in a virtual environment.
	private Long guest = 0L;      //  Time spent for running a virtual CPU or guest OS under the control of the kernel.

	//	The time is measured in USER_HZ (also called Jiffies) which are
	//	typically 1/100ths of a second. USER_HZ is a compile time constant which
	//	can be queried using:
	//      * Shell: getconf CLK_TCK

	//  TODO: Get CLK_TCK value at runtime.


	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Long getNice() {
		return nice;
	}

	public void setNice(Long nice) {
		this.nice = nice;
	}

	public Long getSystem() {
		return system;
	}

	public void setSystem(Long system) {
		this.system = system;
	}

	public Long getIdle() {
		return idle;
	}

	public void setIdle(Long idle) {
		this.idle = idle;
	}

	public Long getIowait() {
		return iowait;
	}

	public void setIowait(Long iowait) {
		this.iowait = iowait;
	}

	public Long getIrq() {
		return irq;
	}

	public void setIrq(Long irq) {
		this.irq = irq;
	}

	public Long getSoftirq() {
		return softirq;
	}

	public void setSoftirq(Long softirq) {
		this.softirq = softirq;
	}

	public Long getSteal() {
		return steal;
	}

	public void setSteal(Long steal) {
		this.steal = steal;
	}

	public Long getGuest() {
		return guest;
	}

	public void setGuest(Long guest) {
		this.guest = guest;
	}
}
