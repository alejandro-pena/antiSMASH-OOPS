package uk.ac.mib.antismashoops.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.ClusterBlastEntry;

@Component
public class ClusterBlastData
{
	private static final Logger logger = LoggerFactory.getLogger(ClusterBlastData.class);

	@Autowired
	private ExternalDataParser edp;

	private static List<ClusterBlastEntry> blastClusters = new ArrayList<>();

	public ClusterBlastData()
	{
	}

	public static List<ClusterBlastEntry> getClusterBlastList()
	{
		return blastClusters;
	}

	public List<ClusterBlastEntry> getClusterBlastData(List<Cluster> clusterData)
	{
		edp.setClusterBlastData(blastClusters, clusterData);
		return blastClusters;
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
