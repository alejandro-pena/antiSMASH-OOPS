package uk.ac.mib.antismashoops.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.mib.antismashoops.core.model.Cluster;

@Component
public class FileDataAnalyser
{
	private static final Logger logger = LoggerFactory.getLogger(FileDataAnalyser.class);
	private static final String REGEX = "(.+)(cluster)(.*)(\\.gbk)";
	private static final String sequenceRegex = "a|g|c|t";

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Autowired
	private ClusterDataParser cdp;

	private static List<Cluster> clusters;

	public List<Cluster> populateClusterNames(String path)
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

	public List<Cluster> populateClusterObjects(String path)
	{
		populateClusterData(clusters);
		return clusters;
	}

	public List<Cluster> populateClusterData(List<Cluster> clusterList)
	{
		for (Cluster c : clusterList)
		{
			c.setGenes(cdp.getGenesData(c.getFile()));
			c.setClusterSequence(getClusterSequence(c.getFile()));
			c.setNumberOfGenes(c.getGenes().size());
			c.setBasePairs(c.getClusterSequence().length());
			c.setGcContent((double) countGC(c.getFile()) / (double) c.getClusterSequence().length());
		}
		return clusterList;
	}

	public int countWord(String word, File file)
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

	public int countGC(File file)
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

	public String getClusterSequence(File file)
	{
		Scanner scanner;
		StringBuilder sb = new StringBuilder();

		try
		{
			scanner = new Scanner(file);

			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				if (nextToken.equalsIgnoreCase("ORIGIN"))
					break;
			}

			scanner.useDelimiter("");

			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				if (nextToken.matches(sequenceRegex))
					sb.append(nextToken);
			}

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return sb.toString();
	}
}
