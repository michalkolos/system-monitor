/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Utils {
	public static String extractStringFromFile(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		String name = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

		return name.replaceAll("\n", "");
	}


	public static Optional<String> extractStringFromFileOptional(File file) {
		String output = null;
		try {
			InputStream inputStream = new FileInputStream(file);
			output = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Optional.ofNullable(output).map(Utils::removeLastEolChar);
	}

	private static String removeLastEolChar(String str) {
		if(str.length() != 0 && str.endsWith(System.lineSeparator())) {
			str = str.substring(0, str.length() - 1);
		}

		return str;
	}


	public static List<File> listDirectoryFiles(File dirFile) {
		FileFilter filter = File::isFile;

		return Optional.ofNullable(dirFile)
				.map(f -> f.listFiles(filter))
				.map(Arrays::asList)
				.orElse(new ArrayList<File>());
	}

}
