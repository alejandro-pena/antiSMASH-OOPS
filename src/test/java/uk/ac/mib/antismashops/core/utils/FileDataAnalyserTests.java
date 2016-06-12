package uk.ac.mib.antismashops.core.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.MvcConfiguration;
import uk.ac.mib.antismashoops.core.utils.FileDataAnalyser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { AntiSmashOopsApplication.class, MvcConfiguration.class })
@WebAppConfiguration
public class FileDataAnalyserTests
{
	private static final Logger logger = LoggerFactory.getLogger(FileDataAnalyserTests.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Autowired
	private FileDataAnalyser fda;

	@Test
	public void countWordTest()
	{
		logger.info("Word count started...");
		File file = new File(uploadPath, "NC_003888.3.cluster002.gbk");
		Assert.assertEquals(24, fda.countWord("gene", file));
	}

	@Test
	public void genericTest()
	{
		// FileDataAnalyser.populateClusterObjects("");
	}

	@Test
	public void populateClusterSequenceTest()
	{
		logger.info("Sequence retrieval started...");
		File file = new File(uploadPath, "NC_003888.3.cluster002.gbk");
		String sequence = fda.getClusterSequence(file);
		logger.info("Hola mundo".substring(0, 10));
		Assert.assertEquals(25538, sequence.length());
	}
}
