package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.ClusterSort;
import uk.ac.mib.antismashoops.core.model.CodonUsage;
import uk.ac.mib.antismashoops.core.model.CodonUsage.Detail;
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
			@RequestParam(value = "codonBias", required = false) int codonBias,
			@RequestParam(value = "refSpecies", required = false) String refSpecies, ModelMap model) throws IOException
	{

		logger.info("Reloading Basic Parameters View");
		Double gcContentRef = 0.0;
		CodonUsage cuRef;
		List<Cluster> clusterData;
		List<Cluster> clustersByNOG;
		List<Cluster> clustersBySL;
		List<Cluster> clustersByGCC;
		List<Cluster> clustersByCB;

		// SORTING DEPENDENT OF SPECIES

		if (refSpecies != null && !refSpecies.equalsIgnoreCase("undefined"))
		{
			gcContentRef = getGcContentBySpecies(refSpecies);
			cuRef = getSpeciesUsageTable(refSpecies);
			clusterData = fda.populateClusterData(gcContentRef, cuRef);
			initialiseScore(clusterData);

			clustersByGCC = new ArrayList<>(clusterData);
			clustersByCB = new ArrayList<>(clusterData);

			Collections.sort(clustersByGCC, ClusterSort.GCCREFSORT);
			Collections.sort(clustersByCB, ClusterSort.CBSORT);

			for (Cluster c : clusterData)
			{
				double score = c.getScore();
				c.setScore(score += ((clustersByCB.indexOf(c) + 1) * 1.0 * codonBias));
			}

		} else
		{
			clusterData = fda.populateClusterData();
			initialiseScore(clusterData);

			clustersByGCC = new ArrayList<>(clusterData);
			Collections.sort(clustersByGCC, ClusterSort.GCCSORT);
		}

		// SORTING INDEPENDENT OF SPECIES

		clustersByNOG = new ArrayList<>(clusterData);
		clustersBySL = new ArrayList<>(clusterData);

		Collections.sort(clustersByNOG, ClusterSort.NOGSORT);
		Collections.sort(clustersBySL, ClusterSort.SLSORT);

		// ASSIGN SCORE TO SORTED INDIVIDUAL PARAMETERS BASED ON USER INTEREST

		for (Cluster c : clusterData)
		{
			double score = c.getScore();
			c.setScore(score += ((clustersByNOG.indexOf(c) + 1) * 1.0 * geneCount));
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

	private void initialiseScore(List<Cluster> clusterData)
	{
		for (Cluster c : clusterData)
		{
			c.setScore(0.0);
		}
	}

	private double getGcContentBySpecies(String refSpecies) throws IOException
	{
		Document doc = Jsoup
				.connect(
						"http://www.kazusa.or.jp/codon/cgi-bin/showcodon.cgi?species=" + refSpecies + "&aa=1&style=GCG")
				.timeout(15000).get();

		Element bodyPage = doc.select("body").first();
		String codingGC = bodyPage.ownText().substring(10, 15);

		return Double.parseDouble(codingGC);
	}

	private CodonUsage getSpeciesUsageTable(String refSpecies) throws IOException
	{
		CodonUsage cu = new CodonUsage();
		LinkedHashMap<String, Detail> table = cu.getUsage();

		Document doc = Jsoup
				.connect(
						"http://www.kazusa.or.jp/codon/cgi-bin/showcodon.cgi?species=" + refSpecies + "&aa=1&style=GCG")
				.timeout(15000).get();

		Element usageTable = doc.select("pre").first();
		Element speciesName = doc.select("i").first();

		cu.setSpecies(speciesName.html());

		String[] codons = usageTable.html().split("\\n");
		codons[0] = "";

		for (String s : codons)
		{
			if (!s.trim().equalsIgnoreCase(""))
			{
				String[] tmp = s.split("\\s+");
				CodonUsage.Detail d = table.get(tmp[1]);
				d.setCodonNumber((int) Double.parseDouble(tmp[2]));
				d.setFrequency(Double.parseDouble(tmp[3]));
			}
		}

		return cu;
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		req.setAttribute("message", exception.getMessage());
		System.out.println(exception.getStackTrace());
		return "error";
	}
}
