package uk.ac.mib.antismashops.core.utils;

import java.io.File;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.utils.FileDataAnalyser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AntiSmashOopsApplication.class)
public class FileDataAnalyserTests
{
	private static final Logger logger = LoggerFactory.getLogger(FileDataAnalyserTests.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Test
	public void wordCount()
	{
		logger.info("Word count started...");
		File file = new File(uploadPath, "NC_003888.3.cluster002.gbk");
		Assert.assertEquals(24, FileDataAnalyser.countWord("gene", file));
	}

	@Test
	public void genericTest()
	{
		FileDataAnalyser.populateClusterFiles(new ArrayList<Cluster>(), "");
	}

}
