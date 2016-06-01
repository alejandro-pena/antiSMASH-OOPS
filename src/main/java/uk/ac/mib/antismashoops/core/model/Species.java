package uk.ac.mib.antismashoops.core.model;

public class Species
{
	private String speciesId;
	private String speciesName;
	private String url;

	public Species()
	{

	}

	public Species(String speciesId, String speciesName, String url)
	{
		this.speciesId = speciesId;
		this.speciesName = speciesName;
		this.url = url;
	}

	public String getSpeciesId()
	{
		return speciesId;
	}

	public void setSpeciesId(String speciesId)
	{
		this.speciesId = speciesId;
	}

	public String getSpeciesName()
	{
		return speciesName;
	}

	public void setSpeciesName(String speciesName)
	{
		this.speciesName = speciesName;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

}
