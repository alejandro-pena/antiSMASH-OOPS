package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.ClusterSort;
import uk.ac.mib.antismashoops.core.utils.FileDataAnalyser;

@Controller
public class DashboardController
{
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private FileDataAnalyser fda;

	@RequestMapping("/dashboard")
	public String showResults(ModelMap model) throws IOException
	{
		logger.info("Loading Basic Parameters View");

		List<Cluster> clusterData = fda.createClusterObjects();
		model.addAttribute("clusterData", clusterData);

		return "dashboard";
	}

	@RequestMapping("/dashboardUpdate")
	public String updateResults(@RequestParam(value = "geneCount", required = false) int geneCount,
			@RequestParam(value = "sequenceLength", required = false) int sequenceLength,
			@RequestParam(value = "gcContent", required = false) int gcContent,
			@RequestParam(value = "codonBias", required = false) int codonBias, ModelMap model) throws IOException
	{

		logger.info("Reloading Basic Parameters View");
		List<Cluster> clusterData = fda.populateClusterData();

		List<Cluster> clustersByNOG = new ArrayList<>(clusterData);
		Collections.sort(clustersByNOG, ClusterSort.NOGSORT);

		List<Cluster> clustersBySL = new ArrayList<>(clusterData);
		Collections.sort(clustersBySL, ClusterSort.SLSORT);

		List<Cluster> clustersByGCC = new ArrayList<>(clusterData);
		Collections.sort(clustersByGCC, ClusterSort.GCCSORT);

		for (Cluster c : clusterData)
		{
			c.setScore((clustersByNOG.indexOf(c) + 1) * 1.0 * geneCount);
		}

		for (Cluster c : clusterData)
		{
			double score = c.getScore();
			c.setScore(score += ((clustersBySL.indexOf(c) + 1) * 1.0 * sequenceLength));
		}

		for (Cluster c : clusterData)
		{
			double score = c.getScore();
			c.setScore(score += ((clustersByGCC.indexOf(c) + 1) * 1.0 * gcContent));
		}

		Collections.sort(clusterData, ClusterSort.SCORESORT);

		model.addAttribute("clusterData", clusterData);

		return "fragments/clusterData :: clusterData";
	}
}
