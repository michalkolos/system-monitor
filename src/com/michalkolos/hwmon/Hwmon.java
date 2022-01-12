/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.hwmon;

import com.michalkolos.utils.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;


/**
 * Extracts hardware data from Hwmon system API.
 */
public class Hwmon {

	/**
	 * Absolute path to Hwmon directory.
	 */
	public static final String HWMON_PATH = "/sys/class/hwmon/";

	public static final int FIELD_NAME_MAX_LEN = 40;
	public static final String FIELD_NAME_TAB = ".";

	private final Map<String, HwmonSubsystem> subsystems = new HashMap<>();


	public Hwmon() throws IOException {
		scanSubsystems();
	}


	/**
	 * Scans Hwmon file tree for subsystems and their fields.
	 * @throws IOException Thrown when Hwmon root directory is inaccessible.
	 */
	private void scanSubsystems() throws IOException {
		File hwmonDir = new File(HWMON_PATH);

		if (!hwmonDir.exists()) {
			throw new IOException("Unable to access Hwmon directory.");
		}

		FileFilter filter = file -> file.isDirectory() &&
				file.getName().matches("hwmon[0-9]+");

		List<File> subsystemDirs = Optional.ofNullable(hwmonDir.listFiles(filter))
				.map(Arrays::asList)
				.orElse(new ArrayList<>());

		for (File subsystemDir : subsystemDirs) {
			try {
				HwmonSubsystem extractedSubsystem = new HwmonSubsystem(subsystemDir);
				subsystems.put(extractedSubsystem.getName(), extractedSubsystem);
			} catch (IOException e) {
				e.printStackTrace();
//				TODO: LOG subsystem access error.
			}
		}

	}

	/**
	 * Returns names of all subsystems reported by the Hwmon API.
	 * @return Set containing all subsystem names.
	 */
	public Set<String> getSubsystemNames() {
		return subsystems.keySet();
	}


	/**
	 * Returns names of fields in a given subsystem.
	 * @param subsystemName Name of the subsystem.
	 * @return  Returns an Optional containing field names in a Set. If the
	 * subsystem name is invalid the Optional will be empty.
	 */
	public Optional<Set<String>> getFieldNamesOptional(String subsystemName) {
		return Optional.ofNullable(subsystems.get(subsystemName))
				.map(HwmonSubsystem::getFields)
				.map(Map::keySet);
	}

	/**
	 * Returns value of a given field.
	 * @param subsystemName Name of the subsystem.
	 * @param fieldName Name of the field in given subsystem.
	 * @return Optional containing either contents of the field's file or empty
	 * if the field is inaccessible.
	 */
	public Optional<String> getFiledValueOptional (String subsystemName, String fieldName) {
		return getFieldFileOptional(subsystemName, fieldName)
				.flatMap(Utils::extractStringFromFileOptional);
	}

	/**
	 * Returns file of a given field.
	 * @param subsystemName Name of the subsystem.
	 * @param fieldName Name of the field in given subsystem.
	 * @return Optional containing either the field's file or empty
	 * if the field is inaccessible.
	 */
	public Optional<File> getFieldFileOptional(String subsystemName, String fieldName) {
		return getSubsystemOptional(subsystemName)
				.map(subsystem -> subsystem.getFields().get(fieldName));
	}

	/**
	 * Returns subsystem object corresponding to a given subsystem name.
	 * @param subsystemName Name of the subsystem.
	 * @return Optional containing either the HwmonSusystem object or empty
	 * if the subsystem name is invalid.
	 */
	public Optional<HwmonSubsystem> getSubsystemOptional (String subsystemName) {
		return Optional.ofNullable(subsystems.get(subsystemName));
	}


	public String subsystemToString(String name) {
		StringBuilder sb = new StringBuilder();

		HwmonSubsystem subsystem = subsystems.get(name);
		if(subsystem != null) {
			sb.append(name)
					.append(": ")
					.append(System.lineSeparator());

			subsystem.getFields().forEach((String fieldName, File fieldFile) ->
					sb.append("\t")
							.append(fieldName)
							.append(FIELD_NAME_TAB.repeat(Math.max(0, FIELD_NAME_MAX_LEN - fieldName.length())))
							.append(Utils.extractStringFromFileOptional(fieldFile).orElse("ERROR"))
							.append(System.lineSeparator())
			);

			sb.append(System.lineSeparator());
		}

		return sb.toString();
	}

	public String allSubsystemsToString() {
		StringBuilder sb = new StringBuilder();
		subsystems.keySet().forEach(key -> sb.append(subsystemToString(key)));

		return sb.toString();
	}
}
