package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.model.CodonUsage;
import uk.ac.mib.antismashoops.core.model.CodonUsage.Detail;
import uk.ac.mib.antismashoops.core.model.Species;

@Controller
public class CodonUsageController
{
	private static final Logger logger = LoggerFactory.getLogger(CodonUsageController.class);

	@RequestMapping("/codonUsage")
	public String help()
	{
		return "codonUsage";
	}

	@RequestMapping(value = "/codonUsage/{species}", method = RequestMethod.GET)
	public String getSpeciesByName(Model model, @PathVariable("species") String species) throws IOException
	{
		List<Species> speciesList = new ArrayList<>();

		Document doc = Jsoup.connect("http://www.kazusa.or.jp/codon/cgi-bin/spsearch.cgi?species=" + species + "&c=i")
				.timeout(15000).get();

		Elements links = doc.select("a[href]");

		if (links.size() == 1)
		{
			speciesList.add(new Species("No data found.", "No data found.", "No data found."));
			model.addAttribute("speciesList", speciesList);
			return "fragments/speciesList :: speciesList";

		}

		links.remove(links.size() - 1);

		for (Element link : links)
		{
			String id = link.attr("href").split("=")[1];
			speciesList.add(new Species(id, link.text(), "/codonUsage/show/" + id));
		}

		model.addAttribute("speciesList", speciesList);
		return "fragments/speciesList :: speciesList";
	}

	@RequestMapping(value = "/codonUsage/show/{species:.+}", method = RequestMethod.GET)
	public String getSpeciesUsageTable(Model model, @PathVariable("species") String species) throws IOException
	{
		CodonUsage cu = new CodonUsage();
		LinkedHashMap<String, Detail> table = cu.getUsage();

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
				CodonUsage.Detail d = table.get(tmp[1]);
				d.setCodonNumber((int) Double.parseDouble(tmp[2]));
				d.setFrequency(Double.parseDouble(tmp[3]));
			}
		}

		model.addAttribute("name", speciesName.html());
		model.addAttribute("table", table);
		return "clusterCodonTable";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		exception.printStackTrace();
		req.setAttribute("message", exception.getMessage());
		return "error";
	}
}
