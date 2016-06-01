package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
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
			speciesList.add(new Species(link.attr("href").split("=")[1], link.text(),
					"http://www.kazusa.or.jp" + link.attr("href")));
		}

		model.addAttribute("speciesList", speciesList);
		return "fragments/speciesList :: speciesList";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		exception.printStackTrace();
		req.setAttribute("message", exception.getMessage());
		return "error";
	}
}
