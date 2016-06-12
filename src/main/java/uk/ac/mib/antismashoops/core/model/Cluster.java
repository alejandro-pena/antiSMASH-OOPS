package uk.ac.mib.antismashoops.core.model;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Cluster
{
	private File file;
	private String name;
	private String recordName;
	private String clusterNumber;
	private int basePairs;
	private List<Gene> genes;
	private int numberOfGenes;
	private double gcContent;
	private String clusterSequence;
	private CodonUsage codonUsage;
	private double score;

	private static final Logger logger = LoggerFactory.getLogger(Cluster.class);

	public Cluster()
	{

	}

	public Cluster(File file)
	{
		this.file = file;
		this.name = removeExtension(file.getName());
		String[] qualifiedName = this.name.split(".c");
		this.recordName = qualifiedName[0];
		Integer clusterNo = Integer.parseInt(qualifiedName[1].replaceAll("\\D+", ""));
		this.clusterNumber = clusterNo.toString();
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRecordName()
	{
		return recordName;
	}

	public void setRecordName(String recordName)
	{
		this.recordName = recordName;
	}

	public String getClusterNumber()
	{
		return clusterNumber;
	}

	public void setClusterNumber(String clusterNumber)
	{
		this.clusterNumber = clusterNumber;
	}

	public int getBasePairs()
	{
		return basePairs;
	}

	public void setBasePairs(int basePairs)
	{
		this.basePairs = basePairs;
	}

	public List<Gene> getGenes()
	{
		return genes;
	}

	public void setGenes(List<Gene> genes)
	{
		this.genes = genes;
	}

	public int getNumberOfGenes()
	{
		return numberOfGenes;
	}

	public void setNumberOfGenes(int numberOfGenes)
	{
		this.numberOfGenes = numberOfGenes;
	}

	public double getGcContent()
	{
		return gcContent;
	}

	public void setGcContent(double gcContent)
	{
		this.gcContent = gcContent;
	}

	public String getClusterSequence()
	{
		return clusterSequence;
	}

	public void setClusterSequence(String clusterSequence)
	{
		this.clusterSequence = clusterSequence;
	}

	@Override
	public String toString()
	{
		return "Cluster [file=" + file + ", numberOfGenes=" + numberOfGenes + "]";
	}

	private static String removeExtension(String name)
	{
		return name.substring(0, name.lastIndexOf('.'));
	}

	public CodonUsage getCodonUsage()
	{
		return codonUsage;
	}

	public void setCodonUsage(CodonUsage codonUsage)
	{
		this.codonUsage = codonUsage;
	}

	public void computeCodonUsage()
	{
		String sequence;
		int codonTotal;
		CodonUsage cu = new CodonUsage();

		for (Gene g : genes)
		{
			sequence = clusterSequence.substring(g.getStartBase() - 1, g.getStopBase());
			if (g.isComplement())
				sequence = getComplementSequence(sequence);

			codonTotal = sequence.length() / 3;

			for (int i = 1; i < codonTotal; i++)
			{
				CodonUsage.Detail d = cu.getUsage().get(sequence.substring(i * 3 - 3, i * 3));
				d.setCodonNumber(d.getCodonNumber() + 1);
			}
		}

		this.setCodonUsage(cu);

		logger.info(cu.toString());

	}

	public String getComplementSequence(String sequence)
	{
		StringBuilder sb = new StringBuilder();
		String reversedSequence = new StringBuilder(sequence).reverse().toString();

		for (int i = 0; i < reversedSequence.length(); i++)
		{
			switch (reversedSequence.charAt(i))
			{
			case 'A':
				sb.append("T");
				break;
			case 'C':
				sb.append("G");
				break;
			case 'G':
				sb.append("C");
				break;
			case 'T':
				sb.append("A");
				break;
			}
		}

		return sb.toString();
	}

	public double getScore()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (clusterNumber == null)
		{
			if (other.clusterNumber != null)
				return false;
		} else if (!clusterNumber.equals(other.clusterNumber))
			return false;
		if (recordName == null)
		{
			if (other.recordName != null)
				return false;
		} else if (!recordName.equals(other.recordName))
			return false;
		return true;
	}

}
