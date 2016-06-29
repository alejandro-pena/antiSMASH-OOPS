package uk.ac.mib.antismashoops.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.CodonUsage;

@Component
public class FileDataAnalyser
{
	private static final Logger logger = LoggerFactory.getLogger(FileDataAnalyser.class);
	private static final String REGEX = "(.+)(cluster)(.*)(\\.gbk)";
	private static final String ZIP_REGEX = "(.+)(\\.zip)";

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Autowired
	private ClusterDataParser cdp;

	private static List<Cluster> clusters;

	public static List<Cluster> getClusterList()
	{
		return clusters;
	}

	public List<Cluster> createClusterObjects()
	{
		logger.info("Cluster population started...");

		for (File parent : new File(uploadPath).listFiles())
		{
			if (parent.isFile() && parent.getName().matches(ZIP_REGEX))
			{
				ZipFileHandler.decompressFile(parent, uploadPath);
			}
		}

		File __MACOSX = new File(uploadPath, "__MACOSX");
		if (__MACOSX.exists())
			delete(__MACOSX);

		clusters = new ArrayList<>();

		for (File parent : new File(uploadPath).listFiles())
		{
			if (parent.isDirectory())
			{

				File folder = new File(uploadPath, parent.getName());
				File[] files = folder.listFiles();

				for (File f : files)
				{
					if (f.getName().matches(REGEX))
					{
						clusters.add(new Cluster(f));
					}
				}
			}
		}

		populateClusterData();

		return clusters;
	}

	public List<Cluster> populateClusterData()
	{
		for (Cluster c : clusters)
		{
			c.setGenes(cdp.getGenesData(c.getFile()));
			c.setClusterSequence(cdp.getClusterSequence(c.getFile()));
			c.setNumberOfGenes(c.getGenes().size());
			c.setBasePairs(c.getCodingSequenceLength());
			c.setGcContent(cdp.getGcContent(c));
		}
		return clusters;
	}

	public List<Cluster> populateClusterData(double gcContentRef, CodonUsage cuRef)
	{
		for (Cluster c : clusters)
		{
			c.setGenes(cdp.getGenesData(c.getFile()));
			c.setClusterSequence(cdp.getClusterSequence(c.getFile()));
			c.setNumberOfGenes(c.getGenes().size());
			c.setBasePairs(c.getCodingSequenceLength());
			c.setGcContent(cdp.getGcContent(c));

			c.setGcContentDiff(gcContentRef);
			c.setCuScoreRef(cuRef);
		}
		return clusters;
	}

	public static void delete(File file)
	{
		if (file.isDirectory())
		{
			if (file.list().length == 0)
				file.delete();
			else
			{
				String files[] = file.list();
				for (String temp : files)
				{
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}

				if (file.list().length == 0)
					file.delete();
			}

		} else
			file.delete();
	}
}
