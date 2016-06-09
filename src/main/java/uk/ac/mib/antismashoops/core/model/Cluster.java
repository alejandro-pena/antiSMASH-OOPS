package uk.ac.mib.antismashoops.core.model;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Cluster implements Comparable<Cluster>, Comparator<Cluster>
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
	public int compareTo(Cluster o)
	{
		return this.numberOfGenes < o.numberOfGenes ? 1 : -1;
	}

	@Override
	public int compare(Cluster o1, Cluster o2)
	{
		return o1.gcContent < o2.gcContent ? 1 : -1;
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

}
