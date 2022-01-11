package com.michalkolos.input;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


/**
 * Represents file asset that can be read in its entirety as a string or as a
 * List of strings, where each string represents one line of the file.
 */
public class LocalFile {
	private File file;


	/**
	 * @param path Absolute path to the file.
	 */
	public LocalFile(String path) {
		file = new File(path);
	}

	/**
	 * @param file File to be read.
	 */
	public LocalFile(File file) {
		this.file = file;
	}


	/**
	 * Reads whole content of the file as a String.
	 * @return File contents as a String.
	 * @throws IOException  Thrown when the file cannot be accessed.
	 */
	public String readString() throws IOException{

		InputStream inputStream = new FileInputStream(this.file);
		String fileContents = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		inputStream.close();

		return fileContents;
	}


	/**
	 * Reads whole content of the file as a List of Strings
	 * @return List of Strings representing individual lines of the file.
	 * @throws IOException  Thrown when the file cannot be accessed.
	 */
	public List<String> readLines() throws IOException{
		return Arrays.asList(readString().split("\n"));
	}
}
