package uk.ac.mib.antismashoops.core.model;

import java.io.File;
import java.util.Comparator;

public class Cluster implements Comparable<Cluster>, Comparator<Cluster>
{
	private String name;
	private File file;
	private int numberOfGenes;
	private int gcContent;

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
		super();
		this.file = file;
		this.numberOfGenes = numberOfGenes;
		this.name = file.getName();
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

	public int getNumberOfGenes()
	{
		return numberOfGenes;
	}

	public void setNumberOfGenes(int numberOfGenes)
	{
		this.numberOfGenes = numberOfGenes;
	}

	@Override
	public String toString()
	{
		return "Cluster [file=" + file + ", numberOfGenes=" + numberOfGenes + "]";
	}

	public int getGcContent()
	{
		return gcContent;
	}

	public void setGcContent(int gcContent)
	{
		this.gcContent = gcContent;
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

}
