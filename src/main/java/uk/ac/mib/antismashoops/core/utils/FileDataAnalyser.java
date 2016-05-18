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

	private static List<Cluster> clusters;

	public static List<Cluster> populateClusterNames(String path)
	{
		logger.info("Cluster population started...");

		clusters = new ArrayList<>();

		File folder = new File(
				"/Users/Alex/Desktop/SpringProjects/antiSMASH-OOPS/appData/NC_003888.3/2acd7e9e-4872-48d4-bae9-cac30ec52622");
		File[] files = folder.listFiles();

		for (File f : files)
		{
			if (f.getName().matches(REGEX))
			{
				clusters.add(new Cluster(f));
			}
		}
		return clusters;
	}

	public static List<Cluster> populateClusterObjects(String path)
	{
		getGeneCount(clusters);
		getGcContent(clusters);
		return clusters;
	}

	public static List<Cluster> getGeneCount(List<Cluster> clusterList)
	{
		for (Cluster c : clusterList)
		{
			c.setNumberOfGenes(countWord("gene", c.getFile()));
		}

		return clusterList;
	}

	public static List<Cluster> getGcContent(List<Cluster> clusterList)
	{
		for (Cluster c : clusterList)
		{
			c.setGcContent(countGC(c.getFile()));
		}

		return clusterList;
	}

	public static int countWord(String word, File file)
	{
		int count = 0;
		Scanner scanner;
		try
		{
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

	public static int countGC(File file)
	{
		int count = 0;
		Scanner scanner;
		char c = 'c';
		char g = 'g';

		try
		{
			scanner = new Scanner(file);
			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				if (nextToken.equalsIgnoreCase("ORIGIN"))
					break;
			}

			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				for (int i = 0; i < nextToken.length(); i++)
					if (nextToken.charAt(i) == c || nextToken.charAt(i) == g)
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
