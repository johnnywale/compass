package org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MigrateTest {
	public static void main(String[] args) throws IOException {
		File f = new File("src/test/java/org");
		List<File> files = new ArrayList<File>();
		listFiles(f, files);
		for (File ff : files) {
			System.out.println(ff);
			ff.delete();
			// ff.delete();

			// String z = ff.toString().replaceAll("main\\\\java",
			// "main\\\\resources");
			// File file = new File(z);
			// file.getParentFile().mkdirs();
			// copyFileUsingFileChannels(ff, file);

		}

	}

	private static void copyFileUsingFileChannels(File source, File dest)

	throws IOException {

		FileChannel inputChannel = null;

		FileChannel outputChannel = null;

		try {

			inputChannel = new FileInputStream(source).getChannel();

			outputChannel = new FileOutputStream(dest).getChannel();

			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

		} finally {

			inputChannel.close();

			outputChannel.close();

		}

	}

	public static void listFiles(File f, List<File> files) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				listFiles(ff, files);
			}
		} else {
			if (!f.getName().endsWith("java")) {
				files.add(f);
			}
		}
	}

}
