/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {
	public static String extractStringFromFile(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		String name = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

		return name.replaceAll("\n", "");
	}
}
