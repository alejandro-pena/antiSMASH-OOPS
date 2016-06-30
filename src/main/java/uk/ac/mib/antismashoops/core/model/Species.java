package uk.ac.mib.antismashoops.core.model;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class Species
{
	private static final Logger logger = LoggerFactory.getLogger(Species.class);

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

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
