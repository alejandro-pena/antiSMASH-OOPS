package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
			@RequestParam(value = "nogOrderValue", required = false) String nogOrder,
			@RequestParam(value = "sequenceLength", required = false) int sequenceLength,
			@RequestParam(value = "slOrderValue", required = false) String slOrder,
			@RequestParam(value = "gcContent", required = false) int gcContent,
			@RequestParam(value = "gccOrderValue", required = false) String gccOrder,
			@RequestParam(value = "codonBias", required = false) int codonBias,
			@RequestParam(value = "cbOrderValue", required = false) String cbOrder,
			@RequestParam(value = "refSpecies", required = false) String refSpecies, ModelMap model) throws IOException
	{

		logger.info("Reloading Basic Parameters View");
		Double gcContentRef = 0.0;
		CodonUsage cuRef;
		List<Cluster> clusterData;

		// SORT BY ONLY THE BASIC THREE PARAMETERS WITHOUT A REFERENCE SPECIES

		if (refSpecies == null || refSpecies.equalsIgnoreCase("undefined"))
		{
			clusterData = fda.populateClusterData();
			initialiseScore(clusterData);
			if (geneCount > 0)
				assignScoreForParameter(clusterData,
						nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
			if (sequenceLength > 0)
				assignScoreForParameter(clusterData,
						slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
			if (gcContent > 0)
				assignScoreForParameter(clusterData,
						gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCSORT : ClusterSort.GCCSORTREV, gcContent);
		}

		// SORT USING ALL PARAMETERS AND WITH A REFERENCE SPECIES
		else
		{
			gcContentRef = getGcContentBySpecies(refSpecies);
			cuRef = getSpeciesUsageTable(refSpecies);
			clusterData = fda.populateClusterData(gcContentRef, cuRef);
			initialiseScore(clusterData);

			if (geneCount > 0)
				assignScoreForParameter(clusterData,
						nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
			if (sequenceLength > 0)
				assignScoreForParameter(clusterData,
						slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
			if (gcContent > 0)
				assignScoreForParameter(clusterData,
						gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCREFSORTREV : ClusterSort.GCCREFSORT, gcContent);
			if (codonBias > 0)
				assignScoreForParameter(clusterData,
						cbOrder.equalsIgnoreCase("a") ? ClusterSort.CBSORT : ClusterSort.CBSORTREV, codonBias);
		}

		// SORT THE FINAL SCORE RESULT

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

	private void assignScoreForParameter(List<Cluster> clusterData, ClusterSort comparator, int parameterWeight)
	{
		List<Cluster> sortedData = new ArrayList<>(clusterData);
		Collections.sort(sortedData, comparator);

		for (Cluster c : clusterData)
		{
			double score = c.getScore();
			c.setScore(score += ((sortedData.indexOf(c) + 1) * 1.0 * parameterWeight));
		}
	}

	private void printScores(List<Cluster> clusterData)
	{
		for (Cluster c : clusterData)
		{
			System.out.println("Cluster: " + c.getClusterNumber() + " Score: " + c.getScore() + " Other score: "
					+ c.getCuScoreRef() + " GC Content: " + c.getGcContentDiff());
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

		Map<String, Integer> aMap = CodonUsage.getAminoacidMap(cu.getUsage());

		for (Entry<String, Detail> codon : cu.getUsage().entrySet())
		{
			Detail d = codon.getValue();
			d.setScorePerAminoacid(d.getCodonNumber() * 100.0 / aMap.get(d.getAminoacid()));
		}

		return cu;
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
