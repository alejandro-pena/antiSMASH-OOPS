package uk.ac.mib.antismashoops.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.mib.antismashoops.core.model.Cluster;

public class FileDataAnalyser
{
	private static final Logger logger = LoggerFactory.getLogger(FileDataAnalyser.class);
	private static final String REGEX = "(.+)(cluster)(.*)(\\.gbk)";

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	private static List<Cluster> clusters = new ArrayList<>();

	public static List<Cluster> populateClusterFiles(List<Cluster> clusterList, String path)
	{
		File folder = new File("/Users/Alex/Desktop/MIBDissertation/testingFiles/outputs/sampleOutput/");
		File[] files = folder.listFiles();

		for (File f : files)
		{
			if (f.getName().matches(REGEX))
			{
				clusters.add(new Cluster(new File(f.getAbsolutePath())));
			}
		}

		getGeneCount(clusters);

		logger.info(clusters.toString());

		return clusterList;
	}

	public static List<Cluster> getGeneCount(List<Cluster> clusterList)
	{
		for (Cluster c : clusterList)
		{
			c.setNumberOfGenes(countWord("gene", c.getName()));
		}

		return clusterList;
	}

	public static int countWord(String word, File file)
	{
		int count = 0;
		Scanner scanner;
		try
		{
			logger.info("Word count for " + word + " has started...");
			scanner = new Scanner(file);
			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				if (nextToken.equalsIgnoreCase(word))
					count++;
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return count;
	}
}
