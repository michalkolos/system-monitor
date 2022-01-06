/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu.data;

import java.util.Optional;

/**
 * Holds parsed temporal data from /proc/stat file.
 */
public class CpuCoreTimes {
	private Long user = null;       //  Time spent with normal processing in user mode.
	private Long nice = null;       //  Time spent with niced processes in user mode.
	private Long system = null;     //  Time spent running in kernel mode.
	private Long idle = null;       //  Time spent in vacations twiddling thumbs.
	private Long iowait = null;     //  Time spent waiting for I/O to completed. This is considered idle time too.
	private Long irq = null;        //  Time spent serving hardware interrupts.
	private Long softirq = null;    //  Time spent serving software interrupts.
	private Long steal = null;      //  Time stolen by other operating systems running in a virtual environment.
	private Long guest = null;      //  Time spent for running a virtual CPU or guest OS under the control of the kernel.

	//	The time is measured in USER_HZ (also called Jiffies) which are
	//	typically 1/100ths of a second. USER_HZ is a compile time constant which
	//	can be queried using:
	//      * Shell: getconf CLK_TCK

	//  TODO: Get CLK_TCK value at runtime.


	public Optional<Long> getUser() {
		return Optional.ofNullable(user);
	}

	public void setUser(long user) {
		this.user = user;
	}

	public Optional<Long> getNice() {
		return Optional.ofNullable(nice);
	}

	public void setNice(long nice) {
		this.nice = nice;
	}

	public Optional<Long> getSystem() {
		return Optional.ofNullable(system);
	}

	public void setSystem(long system) {
		this.system = system;
	}

	public Optional<Long> getIdle() {
		return Optional.ofNullable(idle);
	}

	public void setIdle(long idle) {
		this.idle = idle;
	}

	public Optional<Long> getIowait() {
		return Optional.ofNullable(iowait);
	}

	public void setIowait(long iowait) {
		this.iowait = iowait;
	}

	public Optional<Long> getIrq() {
		return Optional.ofNullable(irq);
	}

	public void setIrq(long irq) {
		this.irq = irq;
	}

	public Optional<Long> getSoftirq() {
		return Optional.ofNullable(softirq);
	}

	public void setSoftirq(long softirq) {
		this.softirq = softirq;
	}

	public Optional<Long> getSteal() {
		return Optional.ofNullable(steal);
	}

	public void setSteal(long steal) {
		this.steal = steal;
	}

	public Optional<Long> getGuest() {
		return Optional.ofNullable(guest);
	}

	public void setGuest(long guest) {
		this.guest = guest;
	}
}
