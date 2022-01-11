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
 * Gathers processor temperature data as reported by the system.
 */
public class CpuTemp {
	public static final String[] THERMAL_SUBSYSTEMS = {"k10temp", "coretemp", "cpu_thermal"};
	public static final String[] THERMAL_FIELDS = {"temp1_input"};

	private final Hwmon hwmon = new Hwmon();
	private final File tempField;



	public CpuTemp() throws IOException {
		tempField = scanTempField(List.of(THERMAL_SUBSYSTEMS), List.of(THERMAL_FIELDS))
				.orElseThrow(() -> new IOException("Unable to access temperature API."));
	}


	public Optional<Float> checkTemp() {

		return Utils.extractStringFromFileOptional(tempField)
				.map(Float::parseFloat)
				.map(f->f/1000);
	}

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
