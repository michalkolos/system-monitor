/*
 * Copyright (c) 2021-2022 by Michal Kolosowski.
 */

package com.michalkolos.hwmon;

import com.michalkolos.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Class representing a subsystem provided by Hwmon api.
 */
public class HwmonSubsystem {

	/**
	 * Path filename of the "name" file that contains the name of the subsystem.
	 */
	public static final String NAME_FILE = "/name";

	private final File dir;
	private final String name;
	private Map<String, File> fields;


	/**
	 * @param dir File object representing base directory of the subsystem.
	 */
	public HwmonSubsystem(File dir) throws IOException {
		this.dir = dir;
		this.name = extractName(dir);
		this.fields = extractFields(dir);
	}


	/**
	 *  Get subsystem's name from the "name" file
	 * @param dir File object representing base directory of the subsystem.
	 * @return  The name of the subsystem
	 * @throws IOException  Thrown when the "name" file is inaccessible.
	 */
	private String extractName(File dir) throws IOException {
		File nameFile = new File(dir.getAbsolutePath() + NAME_FILE);

		return Utils.extractStringFromFile(nameFile);
	}


	/**
	 * Scans files in subsystem's directory to extract fields.
	 * @param dir File object representing base directory of the subsystem.
	 * @return Returns map of field names and their corresponding files.
	 */
	private Map<String, File> extractFields(File dir) {
		Map<String, File> fields = new HashMap<>();
		Utils.listDirectoryFiles(dir).forEach(file -> fields.put(file.getName(), file));

		return fields;
	}


	/**
	 * Returns a system file that represents given field of the subsystem.
	 * @param fieldName Filename of the field to be returned.
	 * @return File object of the field wrapped in an Optional. Optional will be
	 * empty if the field with a given name does not exist.
	 */
	public Optional<File> getFieldFile(String fieldName) {
		return Optional.ofNullable(fields.get(fieldName));
	}


	public Optional<String> getFieldValue(String fieldName) {
		return getFieldFile(fieldName)
				.flatMap(Utils::extractStringFromFileOptional);
	}


	/**
	 * Get main directory of the subsystem.
	 * @return File object representing the directory.
	 */
	public File getDir() {
		return dir;
	}

	/**
	 * Name of the subsystem from the "name" file.
	 * @return Name string.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get all fields available for the subsystem that are accessible as File
	 * objects.
	 * @return List of field objects.
	 */
	public Map<String, File> getFields() {
		return fields;
	}

	public void setFields(Map<String, File> fields) {
		this.fields = fields;
	}
}
