/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu;

import com.michalkolos.hwmon.Hwmon;
import com.michalkolos.utils.Utils;
import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * Gathers CPU temperature data as reported by the system.
 */
public class CpuTemp {

	/**
	 *  Possible names of subsystems providing temperature data.
	 */
	public static final String[] THERMAL_SUBSYSTEMS = {"k10temp", "coretemp", "cpu_thermal"};

	/**
	 *  Possible names of temperature files in the given subsystems.
	 */
	public static final String[] THERMAL_FIELDS = {"temp1_input"};

	private final Hwmon hwmon = new Hwmon();
	private final File tempField;



	public CpuTemp() throws IOException {
		tempField = scanTempField(List.of(THERMAL_SUBSYSTEMS), List.of(THERMAL_FIELDS))
				.orElseThrow(() -> new IOException("Unable to access temperature API."));
	}


	/**
	 * Returns current CPU temp provided by system's Hwmon API.
	 * @return Optional of temperature data. It will be empty if the system file
	 * providing the data is inaccessible.
	 */
	public Optional<Float> checkTemp() {

		return Utils.extractStringFromFileOptional(tempField)
				.map(Float::parseFloat)
				.map(f->f/1000);
	}


	/**
	 * Searches the Hwmon API for a file containing CPU temperature data by matching
	 * subsystem name and field filename with given keywords.
	 * @param subsystemNames List of viable subsystem names. Will be compared to
	 *                       contents of "name" files of all available subsystems.
	 * @param fieldNames List of all viable field filenames.
	 * @return Returns Optional containing random File object from all matching
	 * given criteria. Optional will be empty if there are no matching Files.
	 */
	private Optional<File> scanTempField(List<String> subsystemNames, List<String> fieldNames) {
		return subsystemNames.stream()
				.map(hwmon::getSubsystemOptional)
				.flatMap(Optional::stream)
				.flatMap(hwmonSubsystem -> fieldNames.stream()
						.map(hwmonSubsystem::getFieldFile)
						.flatMap(Optional::stream))
				.findAny();
	}
}
