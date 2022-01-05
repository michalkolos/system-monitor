/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.hwmon.data;


import java.io.File;

public class HwmonField {
	private String value;
	private File file;

	public HwmonField(File file, String value) {
		this.value = value;
		this.file = file;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
