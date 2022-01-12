/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu;

import com.michalkolos.utils.Utils;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Provides CPU clock frequencies per core. The data is gathered from files located in
 * /sys/devices directory. Continuously updated frequency value represented in MHz
 * can be read from "/sys/devices/system/cpu/cpu##/cpufreq/scaling_cur_freq" files.
 */
public class CpuFrequency {

	public static final String SYS_PATH = "/sys/devices/system/cpu";
	public static final String FREQ_FILE_PATH_PART = "/cpufreq/scaling_cur_freq";

	/**
	 * List of directories containing CPU core data.
	 */
	private final List<File> coreFreqFiles;



	public CpuFrequency() throws IOException {

		this.coreFreqFiles = scanCoreDirs().stream()
				.map(dir -> new File(dir.getAbsolutePath() + FREQ_FILE_PATH_PART))
				.collect(Collectors.toList());
	}


	/**
	 * Scans "/sys/devices/system/cpu" directory for matching folders that
	 * represent logical cores of the CPU.
	 * @return List of File objects representing individual CPU core folders.
	 * @throws IOException Thrown when "/sys/devices/system/cpu" is inaccessible.
	 */
	private List<File> scanCoreDirs() throws IOException {
		FileFilter filter = file -> file.isDirectory() && file.getName().matches("cpu[0-9]+");

		File dirFile = new File(SYS_PATH);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			throw new IOException("Cannot access " + SYS_PATH + " directory.");
		}

		return Utils.listDirectoryFiles(dirFile, filter);
	}


	/**
	 * Returns number of CPU logical cores.
	 * @return Number of CPU cores.
	 */
	public int getLogicalCoreNo() {
		return coreFreqFiles.size();
	}


	/**
	 * Returns current clock frequency value for a given logical core.
	 * @param coreNo CPU core id number counting from 0.
	 * @return Most recent CPU core frequency represented in MHz. Returns 0 if
	 * data cannot be read or core number is invalid.
	 */
	public float getCoreFrequency(int coreNo) {
		return getCoreFrequencyOptional(coreNo).orElse(0F);
	}


	/**
	 * Returns current clock frequency value for a given logical core as an
	 * Optional.
	 * @param coreNo CPU core id number counting from 0.
	 * @return Most recent CPU core frequency represented in MHz. Returns empty
	 * Optional if data cannot be read or core number is invalid.
	 */
	public Optional<Float> getCoreFrequencyOptional(int coreNo) {
		return Optional.ofNullable(coreFreqFiles.get(coreNo))
				.flatMap(Utils::extractStringFromFileOptional)
				.map(Float::parseFloat);
	}


	public String toStringCore(int coreNo) {

		return "CPU" +
				coreNo +
				": " +
				getCoreFrequencyOptional(coreNo)
						.map(freq -> freq / 1000)
						.map(Objects::toString)
						.orElse("NULL ") +
				" MHz" +
				System.lineSeparator();
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < coreFreqFiles.size(); i++) {
			sb.append(toStringCore(i));
		}

		return sb.toString();
	}
}
