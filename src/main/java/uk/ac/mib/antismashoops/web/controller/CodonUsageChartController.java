package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.CodonUsage;
import uk.ac.mib.antismashoops.core.model.CodonUsage.Detail;
import uk.ac.mib.antismashoops.core.utils.FileDataAnalyser;

@Controller
public class CodonUsageChartController
{
	private static final Logger logger = LoggerFactory.getLogger(CodonUsageChartController.class);

	@RequestMapping(value = "/codonUsageChart/{clusterName:.+}/{species:.+}", method = RequestMethod.GET)
	public String getCodonUsageChart(ModelMap model, @PathVariable("clusterName") String clusterName,
			@PathVariable("species") String species) throws IOException
	{
		List<Cluster> clusterData = FileDataAnalyser.getClusterList();
		CodonUsage cuRef = new CodonUsage();
		CodonUsage cuBgc;

		Cluster requested = null;
		for (Cluster c : clusterData)
		{
			if (c.getName().equalsIgnoreCase(clusterName))
			{
				requested = c;
				break;
			}
		}

		logger.info("Computing codon usage for Cluster: " + requested.getName());
		requested.computeCodonUsage();

		cuBgc = requested.getCodonUsage();

		LinkedHashMap<String, Detail> tableRef = cuRef.getUsage();
		LinkedHashMap<String, Detail> tableBgc = cuBgc.getUsage();

		Document doc = Jsoup
				.connect("http://www.kazusa.or.jp/codon/cgi-bin/showcodon.cgi?species=" + species + "&aa=1&style=GCG")
				.timeout(15000).get();

		Element usageTable = doc.select("pre").first();
		Element speciesName = doc.select("i").first();

		String[] codons = usageTable.html().split("\\n");
		codons[0] = "";

		for (String s : codons)
		{
			if (!s.trim().equalsIgnoreCase(""))
			{
				String[] tmp = s.split("\\s+");
				CodonUsage.Detail d = tableRef.get(tmp[1]);
				d.setCodonNumber((int) Double.parseDouble(tmp[2]));
				d.setFrequency(Double.parseDouble(tmp[3]));
			}
		}

		Map<String, Integer> aMapRef = CodonUsage.getAminoacidMap(cuRef.getUsage());
		Map<String, Integer> aMapBgc = CodonUsage.getAminoacidMap(cuBgc.getUsage());

		for (Entry<String, Detail> codon : cuRef.getUsage().entrySet())
		{
			Detail d = codon.getValue();
			d.setScorePerAminoacid(d.getCodonNumber() * 100.0 / aMapRef.get(d.getAminoacid()));
		}

		for (Entry<String, Detail> codon : cuBgc.getUsage().entrySet())
		{
			Detail d = codon.getValue();
			d.setScorePerAminoacid(d.getCodonNumber() * 100.0 / aMapBgc.get(d.getAminoacid()));
		}

		model.addAttribute("name", speciesName.html());
		model.addAttribute("tableRef", tableRef);
		model.addAttribute("tableBgc", tableBgc);

		return "codonUsageChart";
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
