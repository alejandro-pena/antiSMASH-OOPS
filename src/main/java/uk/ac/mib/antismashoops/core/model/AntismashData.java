package uk.ac.mib.antismashoops.core.model;

import java.util.List;

public class AntismashData
{
	private List<Cluster> clusters;
	private List<Cluster> subClusters;
	private List<Cluster> knownClusters;

	private static AntismashData instance = new AntismashData();

	private AntismashData()
	{
	}

	public static AntismashData getInstance()
	{
		return instance;
	}

	public List<Cluster> getClusters()
	{
		return clusters;
	}

	public void setClusters(List<Cluster> clusters)
	{
		this.clusters = clusters;
	}

	public List<Cluster> getSubClusters()
	{
		return subClusters;
	}

	public void setSubClusters(List<Cluster> subClusters)
	{
		this.subClusters = subClusters;
	}

	public List<Cluster> getKnownClusters()
	{
		return knownClusters;
	}

	public void setKnownClusters(List<Cluster> knownClusters)
	{
		this.knownClusters = knownClusters;
	}

}
