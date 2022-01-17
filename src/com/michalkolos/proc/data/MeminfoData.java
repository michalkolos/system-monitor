/*
 * Copyright (c) 2022 by Michal Kolosowski.
 */

package com.michalkolos.proc.data;

public class MeminfoData {
	private long memTotal = 0;  //  Total usable ram (i.e. physical ram minus a few reserved bits and the kernel binary code)
	private long memFree = 0;   //  Is sum of LowFree+HighFree (overall stat)



	private long memAvailable = 0;


}
