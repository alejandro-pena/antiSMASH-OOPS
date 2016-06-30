package uk.ac.mib.antismashoops.core.model;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class Gene
{
	private static final Logger logger = LoggerFactory.getLogger(Gene.class);

	private String geneId;
	private String synonym;
	private int startBase;
	private int stopBase;
	private boolean complement;

	public Gene()
	{

	}

	public Gene(String geneId, String synonym, int startBase, int stopBase, boolean complement)
	{
		this.geneId = geneId;
		this.synonym = synonym;
		this.startBase = startBase;
		this.stopBase = stopBase;
		this.complement = complement;
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

	public boolean isComplement()
	{
		return complement;
	}

	public void setComplement(boolean complement)
	{
		this.complement = complement;
	}

	@Override
	public String toString()
	{
		return "Gene [geneId=" + geneId + ", synonym=" + synonym + ", startBase=" + startBase + ", stopBase=" + stopBase
				+ ", complement=" + complement + "]";
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
