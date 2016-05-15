package uk.ac.mib.antismashoops.core.model;

import java.io.File;

public class Cluster
{
	private File file;
	private int numberOfGenes;

	public Cluster()
	{

	}

	public Cluster(File file)
	{
		this.file = file;
	}

	public Cluster(File file, int numberOfGenes)
	{
		super();
		this.file = file;
		this.numberOfGenes = numberOfGenes;
	}

	public File getName()
	{
		return file;
	}

	public void setName(File file)
	{
		this.file = file;
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

}
