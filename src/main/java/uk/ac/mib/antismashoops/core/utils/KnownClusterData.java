package uk.ac.mib.antismashoops.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.model.KnownClusterEntry;

@Component
public class KnownClusterData
{
	private static final Logger logger = LoggerFactory.getLogger(KnownClusterData.class);

	@Autowired
	private ExternalDataParser edp;

	private static List<KnownClusterEntry> knownClusters = new ArrayList<>();

	public KnownClusterData()
	{
	}

	public static List<KnownClusterEntry> getKnownClusterList()
	{
		return knownClusters;
	}

	public List<KnownClusterEntry> getKnownClusterData()
	{
		edp.setKnownClusterData(knownClusters);
		return knownClusters;
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
