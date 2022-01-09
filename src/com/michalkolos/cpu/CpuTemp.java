/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Gathers processor temperature data as reported by the system.
 */
public class CpuTemp {

	public static final String THERMAL_FILE_PATH = "/sys/class/thermal/thermal_zone0/temp";

	public static final String HWMON_PATH = "/sys/class/hwmon/";
	public static final String HWMON_TEMP_FILE = "/temp1_input";
	public static final String HWMON_NAME_FILE = "/name";
	public static final String[] HWMON_CPU_NAMES = {"k10temp", "cpu_thermal"};

	private File hwmonCpuSubsystemDir;




	private String extractStringFromFile(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		String name = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

		return name.replaceAll("\n", "");
	}

	private File detectHwmonSubsystem(String[] deviceNames) {

		File subsystemDir = null;

		try {
			File hwmonDir = new File(HWMON_PATH);
			FileFilter filter = file -> file.isDirectory() && file.getName().matches("hwmon[0-9]+");
			File[] allSubsystemDirs = hwmonDir.listFiles(filter);

			if (allSubsystemDirs != null) {

				for(File subsystemDirCandidate : allSubsystemDirs) {

					String extractedName = extractStringFromFile(
							new File(subsystemDirCandidate.getAbsolutePath() + HWMON_NAME_FILE));

					boolean isNameMatching = Arrays.stream(HWMON_CPU_NAMES)
							.anyMatch(name -> name.matches(extractedName));

					if(isNameMatching) {
						subsystemDir = subsystemDirCandidate;
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return subsystemDir;
	}


	public void dataAcquisitionLoop() throws IOException {

	}
}
