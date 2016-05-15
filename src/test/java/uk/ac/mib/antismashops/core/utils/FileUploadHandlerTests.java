package uk.ac.mib.antismashops.core.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.core.utils.FileUploadHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AntiSmashOopsApplication.class)
public class FileUploadHandlerTests
{
	private static final Logger logger = LoggerFactory.getLogger(FileUploadHandlerTests.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	// private static final String fileName = "NC_003888.3.zip";
	private static final String fileName = "Archive.zip";
	private File compressedFile;
	private String destinationPath;

	@Before
	public void setup()
	{
		compressedFile = new File(uploadPath, fileName);
		destinationPath = uploadPath + compressedFile.getName().substring(0, compressedFile.getName().lastIndexOf("."));
	}

	@Test
	public void decompressFileTest()
	{
		logger.info("Start of file decompression...");
		logger.info(compressedFile.getAbsolutePath());

		FileUploadHandler.decompressFile(compressedFile);

		logger.info("...finished file decompression.");

		Assert.assertEquals("Archive", new File(destinationPath).getName());

		// Count and ensure that the unzipped files are complete excluding
		// system files from the count
		File[] fileList = new File(destinationPath).listFiles();
		int count = 0;
		for (File file : fileList)
		{
			if (!file.isHidden())
				count++;
		}

		Assert.assertEquals(6, count);
	}

}