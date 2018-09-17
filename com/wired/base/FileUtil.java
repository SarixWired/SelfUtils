package com.wired.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class FileUtil {

	public static void splitFile(String srcPath, String destPath) throws IOException {
		File file = new File(srcPath);
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new RuntimeException("The File is a Directory ! Can't be splited !");
			}
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			FileOutputStream fos = null;
			CheckedOutputStream cos = null;
			byte[] buf = new byte[1024 * 1024 * 32];
			Integer parts = 1;
			Integer len;
			while ((len = bis.read(buf)) != -1) {
				fos = new FileOutputStream(destPath + File.separator + file.getName() + ".part" + (parts++));
				cos = new CheckedOutputStream(fos, new CRC32());
				cos.write(buf, 0, len);
				cos.close();
				fos.close();
			}
			bis.close();
		} else {
			throw new RuntimeException("The File is not found ! ");
		}
	}

	public static void sequenceFile(String srcDirPath) throws IOException {
		File file = new File(srcDirPath);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				String destFilename = files[0].getName().substring(0, files[0].getName().lastIndexOf('.'));
				CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFilename), new CRC32());
				List<FileInputStream> fizs = new ArrayList<FileInputStream>();
				for (File part : files) {
					fizs.add(new FileInputStream(part));
				}
				Enumeration<FileInputStream> en = Collections.enumeration(fizs);
				SequenceInputStream sis = new SequenceInputStream(en);
				Integer len;
				while ((len = sis.read()) != -1) {
					cos.write(len);
				}
				sis.close();
				cos.close();
			} else {
				throw new RuntimeException("The File is a File ! Can't be sequenced !");
			}
		} else {
			throw new RuntimeException("The File is not found ! ");
		}
	}

	public static void copyDir(String srcPath, String destPath) throws IOException {
		File file = new File(srcPath);
		String[] filePaths = file.list();
		if (!(new File(destPath)).exists()) {
			(new File(destPath)).mkdir();
		}
		for (String filePath : filePaths) {
			if ((new File(srcPath + File.separator + filePath)).isDirectory()) {
				copyDir(srcPath + File.separator + filePath, destPath + File.separator + filePath);
			}
			if (new File(srcPath + File.separator + filePath).isFile()) {
				copyFile(srcPath + File.separator + filePath, destPath + File.separator + filePath);
			}
		}

	}

	public static void copyFile(String srcFile, String destFile) throws IOException {
		FileInputStream infile = new FileInputStream(srcFile);
		FileOutputStream outfile = new FileOutputStream(destFile, true);
		byte[] bb = new byte[1024];
		int lenth = 0;
		while ((lenth = infile.read(bb)) != -1) {
			outfile.write(bb, 0, lenth);
		}
		outfile.flush();
		outfile.close();
		infile.close();
	}
}
