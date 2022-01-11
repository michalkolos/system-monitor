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

	public static final int FIELD_NAME_MAX_LEN = 40;
	public static final String FIELD_NAME_TAB = ".";

	private final Map<String, HwmonSubsystem> subsystems = new HashMap<>();


	public Hwmon() {
		scanSubsystems();
	}

	private void scanSubsystems() {
		File hwmonDir = new File(HWMON_PATH);

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

//      TODO: delete bellow:
//		for(File dir : subsystemDirs) {
//			File nameFile = new File(dir.getAbsolutePath() + NAME_FILE);
//
//			Utils.extractStringFromFileOptional(nameFile).ifPresent(name ->
//					subsystems.put(name, new HwmonSubsystem(dir, name)));
//		}
	}





	public Set<String> getSubsystemNames() {
		return subsystems.keySet();
	}

	public Optional<Set<String>> getFieldNamesOptional(String subsystemName) {
		return Optional.ofNullable(subsystems.get(subsystemName))
				.map(HwmonSubsystem::getFields)
				.map(Map::keySet);
	}

//  TODO: Delete bellow:
//	public String getFieldValue(String subsystemName, String fieldName) {
//		HwmonSubsystem subsystem = subsystems.get(subsystemName);
//		String fieldVal = null;
//
//		if(subsystem != null) {
//			fieldVal = subsystem.getFields().get(fieldName).getValue();
//		}
//
//		return fieldVal;
//	}

	public Optional<String> getFiledValueOptional (String subsystemName, String fieldName) {
		return getFieldFileOptional(subsystemName, fieldName)
				.flatMap(Utils::extractStringFromFileOptional);
	}

	public Optional<File> getFieldFileOptional(String subsystemName, String fieldName) {
		return getSubsystemOptional(subsystemName)
				.map(subsystem -> subsystem.getFields().get(fieldName));
	}

	public Optional<HwmonSubsystem> getSubsystemOptional (String subsystemName) {
		return Optional.ofNullable(subsystems.get(subsystemName));
	}

//	TODO: Delete bellow:
//	public File getFieldFile(String subsystemName, String fieldName) {
//		HwmonSubsystem subsystem = subsystems.get(subsystemName);
//		File fieldFile = null;
//
//		if(subsystem != null) {
//			fieldFile = subsystem.getFields().get(fieldName).getFile();
//		}
//
//		return fieldFile;
//	}


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
