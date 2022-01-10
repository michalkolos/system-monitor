/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu;

import com.michalkolos.hwmon.Hwmon;
import com.michalkolos.utils.Utils;

import java.io.*;
import java.util.Optional;

/**
 * Gathers processor temperature data as reported by the system.
 */
public class CpuTemp {

//	public static final String THERMAL_SUBSYSTEM = "cpu_thermal";
//	public static final String THERMAL_SUBSYSTEM = "coretemp";
	public static final String THERMAL_SUBSYSTEM = "k10temp";

	public static final String THERMAL_FIELD = "temp1_input";

	private final Hwmon hwmon = new Hwmon();
	private final File tempField;



	public CpuTemp() {
		tempField = hwmon.getFieldFile(THERMAL_SUBSYSTEM, THERMAL_FIELD);
	}

	public Optional<Float> checkTemp() {
		Optional<Float> temp = Optional.empty();
		try {
			String tempString = Utils.extractStringFromFile(tempField);
			temp = Optional.ofNullable(Float.parseFloat(tempString) / 1000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return temp;
	}
}
