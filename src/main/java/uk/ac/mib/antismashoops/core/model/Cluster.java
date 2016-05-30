package uk.ac.mib.antismashoops.core.model;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Cluster implements Comparable<Cluster>, Comparator<Cluster>
{
	private String name;
	private File file;
	private int basePairs;
	private List<Gene> genes;
	private int numberOfGenes;
	private int gcContent;
	private String sequence;

	public Cluster()
	{

	}

	public Cluster(File file)
	{
		this.file = file;
		this.name = file.getName();
	}

	public Cluster(File file, int numberOfGenes)
	{
		this.file = file;
		this.numberOfGenes = numberOfGenes;
		this.name = file.getName();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
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

	public int getGcContent()
	{
		return gcContent;
	}

	public void setGcContent(int gcContent)
	{
		this.gcContent = gcContent;
	}

	public String getSequence()
	{
		return sequence;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
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
}
