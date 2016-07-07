package uk.ac.mib.antismashoops.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class KnownClusterEntry
{
	private File file;
	private String recordName;
	private int clusterNumber;
	private List<Gene> clusterGenes;
	private List<ClusterFw> clusterHits;

	public KnownClusterEntry()
	{
		clusterGenes = new ArrayList<>();
		clusterHits = new ArrayList<>();
	}

	public KnownClusterEntry(File file, String recordName, int clusterNumber)
	{
		this();
		this.file = file;
		this.setRecordName(recordName);
		this.setClusterNumber(clusterNumber);
	}

	public double getBestMatchScore(double preferredSimilarity)
	{
		if (this.getClusterHits().size() == 0)
			return 100.0;

		ClusterFw mostSimilar = getClusterHits().get(0);
		double score = Math
				.abs(preferredSimilarity - ((mostSimilar.getBlastHits().size() * 100 / getClusterGenes().size())
						* mostSimilar.getBlastHitScore() / 100));

		for (ClusterFw c : getClusterHits())
		{
			double newScore = Math.abs(preferredSimilarity
					- ((c.getBlastHits().size() * 100 / getClusterGenes().size()) * c.getBlastHitScore() / 100));
			if (newScore < score)
			{
				mostSimilar = c;
				score = newScore;
			}
		}
		return score;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getRecordName()
	{
		return recordName;
	}

	public void setRecordName(String recordName)
	{
		this.recordName = recordName;
	}

	public int getClusterNumber()
	{
		return clusterNumber;
	}

	public void setClusterNumber(int clusterNumber)
	{
		this.clusterNumber = clusterNumber;
	}

	public List<Gene> getClusterGenes()
	{
		return clusterGenes;
	}

	public void setClusterGenes(List<Gene> clusterGenes)
	{
		this.clusterGenes = clusterGenes;
	}

	public List<ClusterFw> getClusterHits()
	{
		return clusterHits;
	}

	public void setClusterHits(List<ClusterFw> clusterHits)
	{
		this.clusterHits = clusterHits;
	}

	@Override
	public String toString()
	{
		return "KnownClusterEntry [file=" + file + ", recordName=" + recordName + ", clusterNumber=" + clusterNumber
				+ "]";
	}

}
