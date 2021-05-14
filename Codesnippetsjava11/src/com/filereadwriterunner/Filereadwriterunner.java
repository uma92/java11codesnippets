package com.filereadwriterunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Filereadwriterunner {

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("./resources/sample.txt");
		String filecontent = Files.readString(path);
		System.out.println(filecontent);
		String newfilecontent = filecontent.replace("line1", "new line1");

		Path newfilepath = Paths.get("./resources/newsample.txt");
		Files.writeString(newfilepath, newfilecontent);
	}

}
