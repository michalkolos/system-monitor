/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.hwmon.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HwmonSubsystem {
	private final File dir;
	private final String name;
	private Map<String, HwmonField> fields = new HashMap<>();

	public HwmonSubsystem(File dir, String name) {
		this.dir = dir;
		this.name = name;
	}

	public File getDir() {
		return dir;
	}

	public String getName() {
		return name;
	}

	public Map<String, HwmonField> getFields() {
		return fields;
	}

	public void setFields(Map<String, HwmonField> fields) {
		this.fields = fields;
	}
}
