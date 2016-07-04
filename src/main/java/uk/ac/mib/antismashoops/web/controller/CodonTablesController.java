package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.utils.FileDataAnalyser;

@Controller
public class CodonTablesController
{
	private static final Logger logger = LoggerFactory.getLogger(CodonTablesController.class);

	@RequestMapping(value = "/codonTable/{cluster:.+}", method = RequestMethod.GET)
	public String getCodonUsageInfo(Model model, @PathVariable("cluster") String cluster) throws IOException
	{
		List<Cluster> clusterData = FileDataAnalyser.getClusterList();

		Cluster requested = null;
		for (Cluster c : clusterData)
		{
			if (c.getName().equalsIgnoreCase(cluster))
			{
				requested = c;
				break;
			}
		}

		logger.info("Computing codon usage for Cluster: " + requested.getName());
		requested.computeCodonUsage();

		model.addAttribute("name", requested.getRecordName() + " - Cluster" + requested.getClusterNumber());
		model.addAttribute("table", requested.getCodonUsage().getUsage());

		return "clusterCodonTable";
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
