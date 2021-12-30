/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Provides CPU clock frequencies per core. The data is gathered from files located in
 * /sys/devices directory. Continuously updated frequency value represented in MHz
 * can be read from "/sys/devices/system/cpu/cpu##/cpufreq/scaling_cur_freq" files.
 */
public class CpuFrequency {

	public static final String SYS_PATH = "/sys/devices/system/cpu";


	/**
	 * Number of CPU logical cores calculated by counting directories containing
	 * CPU core data.
	 */
	private int logicalCores = 0;

	/**
	 * List of directories containing CPU core data.
	 */
	private List<File> cpuDirs = new ArrayList<>();


	/**
	 * List of parsed CPU core clock frequencies.
	 */
	private final List<Integer> coreFrequencies;



	public CpuFrequency() {
		File sysDir = new File(SYS_PATH);

		if(sysDir.exists() && sysDir.isDirectory()) {

			///  Filter that looks for CPU core directories named as cpu<core_number>
			FileFilter filter = file -> file.isDirectory() && file.getName().matches("cpu[0-9]+");

			File[] files = sysDir.listFiles(filter);

			if(files != null){
				this.cpuDirs = Arrays.asList(files);
			}

			logicalCores = this.cpuDirs.size();
		}

		this.coreFrequencies = new ArrayList<>(Collections.nCopies(logicalCores, 0));
	}


	/**
	 * Method that needs to be called periodically  to gather current frequency
	 * value. This data is provided by the system in separate "scaling_cur_freq"
	 * files for each core.
	 * @throws IOException Thrown when one of the 'scaling_cur_freq' files is
	 * unavailable.
	 */
	public void dataAcquisitionLoop() throws IOException {
		for(File dir : cpuDirs) {

			File mhzFile = new File(dir.getAbsolutePath() + "/cpufreq/scaling_cur_freq");
			InputStream inputStream = new FileInputStream(mhzFile);
			String mhzText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

			//  Parsing frequency data from string to number variable requires
			//  removing trailing end of line ("\n") character.
			int mhzNum = Integer.parseInt(mhzText.substring(0, mhzText.length() - 1));

			//  Getting CPU core ID number from its folder name in
			//  "/sys/devices/system/cpu" directory.
			int coreNo = Integer.parseInt(dir.getName().substring(3));

			coreFrequencies.set(coreNo, mhzNum);
		}
	}


	public int getLogicalCores() {
		return logicalCores;
	}


	/**
	 * Returns most recent clock frequency value for a given logical core.
	 * @param coreNo CPU core id number counting from 0.
	 * @return Most recent CPU core frequency represented in MHz.
	 */
	public int getCoreFrequency(int coreNo) {
		if(coreNo >= 0 && coreNo < this.coreFrequencies.size()) {
			return coreFrequencies.get(coreNo);
		} else {
			return 0;
		}
	}
}
