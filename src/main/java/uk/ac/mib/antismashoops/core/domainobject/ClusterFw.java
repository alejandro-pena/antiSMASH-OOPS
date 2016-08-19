package uk.ac.mib.antismashoops.core.domainobject;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ClusterFw
{
	private String name;
	private String source;
	private String type;
	private int proteinsBlasted;
	private int blastScore;
	List<Gene> geneList;
	List<BlastHit> blastHits;

	public ClusterFw()
	{
		geneList = new ArrayList<>();
		blastHits = new ArrayList<>();
	}

	public ClusterFw(String name, String source)
	{
		this();
		this.name = name;
		this.source = source;
	}

	public double getBlastHitScore()
	{
		double score = 0.0;

		for (BlastHit bhe : blastHits)
		{
			score += (bhe.getCoveragePercentage() * bhe.getIdentityPercentage()) / 100;
		}

		return score / blastHits.size();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public int getProteinsBlasted()
	{
		return proteinsBlasted;
	}

	public void setProteinsBlasted(int proteinsBlasted)
	{
		this.proteinsBlasted = proteinsBlasted;
	}

	public int getBlastScore()
	{
		return blastScore;
	}

	public void setBlastScore(int blastScore)
	{
		this.blastScore = blastScore;
	}

	public List<Gene> getGeneList()
	{
		return geneList;
	}

	public void setGeneList(List<Gene> geneList)
	{
		this.geneList = geneList;
	}

	public List<BlastHit> getBlastHits()
	{
		return blastHits;
	}

	public void setBlastHits(List<BlastHit> blastHits)
	{
		this.blastHits = blastHits;
	}
}
