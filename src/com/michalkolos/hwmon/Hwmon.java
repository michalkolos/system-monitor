/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.hwmon;

import com.michalkolos.hwmon.data.HwmonField;
import com.michalkolos.hwmon.data.HwmonSubsystem;
import com.michalkolos.utils.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

public class Hwmon {

	public static final String HWMON_PATH = "/sys/class/hwmon/";
	public static final String NAME_FILE = "/name";
	public static final int FIELD_NAME_MAX_LEN = 40;
	public static final String FIELD_NAME_TAB = ".";

	private final Map<String, HwmonSubsystem> subsystems = new HashMap<>();


	public Hwmon() {
		scan();
	}

	private void listSubsystems() {
		File hwmonDir = new File(HWMON_PATH);

		FileFilter filter = file -> file.isDirectory() &&
				file.getName().matches("hwmon[0-9]+");

		List<File> subsystemDirs = Optional.ofNullable(hwmonDir.listFiles(filter))
				.map(Arrays::asList)
				.orElse(new ArrayList<>());

		for(File dir : subsystemDirs) {
			File nameFile = new File(dir.getAbsolutePath() + NAME_FILE);

			Utils.extractStringFromFileOptional(nameFile).ifPresent(name ->
					subsystems.put(name, new HwmonSubsystem(dir, name)));
		}
	}

	private void scanSubsystemFields() throws IOException {

		for(HwmonSubsystem subsystem : subsystems.values()) {
			List<File> fieldFiles = Optional.ofNullable(subsystem.getDir())
					.map(Utils::listDirectoryFiles)
					.orElse(new ArrayList<>());

			fieldFiles.forEach(file ->
				Utils.extractStringFromFileOptional(file).ifPresent(s ->
					subsystem.getFields().put(file.getName(), new HwmonField(file, s))));
		}
	}

	private void scan() {
		try {
			listSubsystems();
			scanSubsystemFields();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String subsystemToString(String name) {
		StringBuilder sb = new StringBuilder();

		HwmonSubsystem subsystem = subsystems.get(name);
		if(subsystem != null) {
			sb.append(name)
					.append(": ")
					.append(System.lineSeparator());

			subsystem.getFields().forEach((String fieldName, HwmonField field) ->
				sb.append("\t")
						.append(fieldName)
						.append(FIELD_NAME_TAB.repeat(Math.max(0, FIELD_NAME_MAX_LEN - fieldName.length())))
						.append(field.getValue())
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

	public Set<String> getSubsystemNames() {
		return subsystems.keySet();
	}

	public Set<String> getFieldNames(String subsystemName) {
		Set<String> fieldNames = new HashSet<>();

		HwmonSubsystem subsystem = subsystems.get(subsystemName);
		if(subsystem != null) {
			fieldNames = subsystem.getFields()
					.keySet();
		}

		return fieldNames;
	}

	public String getFieldValue(String subsystemName, String fieldName) {
		HwmonSubsystem subsystem = subsystems.get(subsystemName);
		String fieldVal = null;

		if(subsystem != null) {
			fieldVal = subsystem.getFields().get(fieldName).getValue();
		}

		return fieldVal;
	}

	public File getFieldFile(String subsystemName, String fieldName) {
		HwmonSubsystem subsystem = subsystems.get(subsystemName);
		File fieldFile = null;

		if(subsystem != null) {
			fieldFile = subsystem.getFields().get(fieldName).getFile();
		}

		return fieldFile;
	}
}
