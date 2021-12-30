/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu.data;

public class CpuCoreUsageDetails {
	private float totalUsage = 0.0f;
	private float ioUsage = 0.0f;
	private float userUsage = 0.0f;
	private float systemUsage = 0.0f;
	private float hardIrqUsage = 0.0f;
	private float softIrqUsage = 0.0f;


	public float getTotalUsage() {
		return totalUsage;
	}

	public void setTotalUsage(float totalUsage) {
		this.totalUsage = totalUsage;
	}

	public float getIoUsage() {
		return ioUsage;
	}

	public void setIoUsage(float ioUsage) {
		this.ioUsage = ioUsage;
	}

	public float getUserUsage() {
		return userUsage;
	}

	public void setUserUsage(float userUsage) {
		this.userUsage = userUsage;
	}

	public float getSystemUsage() {
		return systemUsage;
	}

	public void setSystemUsage(float systemUsage) {
		this.systemUsage = systemUsage;
	}

	public float getHardIrqUsage() {
		return hardIrqUsage;
	}

	public void setHardIrqUsage(float hardIrqUsage) {
		this.hardIrqUsage = hardIrqUsage;
	}

	public float getSoftIrqUsage() {
		return softIrqUsage;
	}

	public void setSoftIrqUsage(float softIrqUsage) {
		this.softIrqUsage = softIrqUsage;
	}
}
