package uk.ac.mib.antismashoops.core.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class FileUploadHandler
{
	private static final Logger logger = LoggerFactory.getLogger(FileUploadHandler.class);

	public static void decompressFile(File compressedFile, String uploadPath)
	{
		String destinationPath = uploadPath
				+ compressedFile.getName().substring(0, compressedFile.getName().lastIndexOf("."));

		try
		{
			ZipFile zipFile = new ZipFile(compressedFile);
			if (zipFile.isEncrypted())
			{
				logger.info("The file is password protected... Unable to decompress.");
			}

			zipFile.extractAll(destinationPath);

		} catch (ZipException e)
		{
			logger.error(e.getMessage());
		}
	}
}
