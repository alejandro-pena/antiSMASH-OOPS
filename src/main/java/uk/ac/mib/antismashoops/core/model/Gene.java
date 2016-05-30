package uk.ac.mib.antismashoops.core.model;

public class Gene
{
	private String geneId;
	private String synonym;
	private int startBase;
	private int stopBase;
	private String sequence;

	public Gene(String geneId, String synonym, int startBase, int stopBase, String sequence)
	{
		this.geneId = geneId;
		this.synonym = synonym;
		this.startBase = startBase;
		this.stopBase = stopBase;
		this.sequence = sequence;
	}

	public String getGeneId()
	{
		return geneId;
	}

	public void setGeneId(String geneId)
	{
		this.geneId = geneId;
	}

	public String getSynonym()
	{
		return synonym;
	}

	public void setSynonym(String synonym)
	{
		this.synonym = synonym;
	}

	public int getStartBase()
	{
		return startBase;
	}

	public void setStartBase(int startBase)
	{
		this.startBase = startBase;
	}

	public int getStopBase()
	{
		return stopBase;
	}

	public void setStopBase(int stopBase)
	{
		this.stopBase = stopBase;
	}

	public String getSequence()
	{
		return sequence;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}
}
