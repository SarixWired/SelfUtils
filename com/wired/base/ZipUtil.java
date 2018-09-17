package com.wired.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	private static String startDir;

	public static void mkZip(String srcPath, String zipPathName) {
		File file = new File(srcPath);
		if (file.exists()) {
			System.out.println(" Starting to zip directory " + srcPath + "...");
			try {
				FileOutputStream fos = new FileOutputStream(new File(zipPathName));
				CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
				ZipOutputStream zos = new ZipOutputStream(cos);
				String basedir = "";
				startDir = file.getParent() + File.separator;
				compress(file, zos, basedir);
				zos.close();
				cos.close();
				fos.close();
				System.out.println(" Finish zipping directory " + srcPath + "...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException(" The file is not existed ! ");
		}
	}

	public static void unzip(String srcFile, String destDirPath) throws IOException {
		File file = new File(srcFile);
		if (file.exists()) {
			System.out.println(" Starting to unzip file " + srcFile + "...");
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				File targetFile = new File(destDirPath + File.separator + entry.getName());
				if (entry.isDirectory()) {
					targetFile.mkdirs();
				} else {
					if (!targetFile.getParentFile().exists()) {
						targetFile.getParentFile().mkdirs();
					}
					targetFile.createNewFile();
					BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
					FileOutputStream fos = new FileOutputStream(targetFile);
					Integer len;
					while ((len = bis.read()) != -1) {
						fos.write(len);
					}
					fos.close();
					bis.close();
				}
			}
			zipFile.close();
			System.out.println(" Finish unzipping file " + srcFile + "...");
		} else {
			throw new RuntimeException(" The file is not existed ! ");
		}
	}

	private static void compress(File source, ZipOutputStream out, String basedir) throws IOException {
		if (!source.exists()) {
			return;
		}
		if (source.isDirectory()) {
			File[] files = source.listFiles();
			for (File file : files) {
				compress(file, out, basedir);
			}
		} else {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
			ZipEntry entry = new ZipEntry(basedir + source.getPath().substring(startDir.length()));
			out.putNextEntry(entry);
			Integer len;
			while ((len = bis.read()) != -1) {
				out.write(len);
			}
			out.closeEntry();
			bis.close();
		}
	}

}
