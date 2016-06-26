package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.model.Species;

@Controller
public class SpeciesDropDownController
{
	private static final Logger logger = LoggerFactory.getLogger(SpeciesDropDownController.class);

	@RequestMapping(value = "/species/{species}", method = RequestMethod.GET)
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
			return "fragments/speciesDD :: speciesDD";
		}

		links.remove(links.size() - 1);

		for (Element link : links)
		{
			String id = link.attr("href").split("=")[1];
			speciesList.add(new Species(id, link.text().substring(0, link.text().indexOf('[') - 1), ""));
		}

		model.addAttribute("speciesList", speciesList);
		return "fragments/speciesDD :: speciesDD";
	}
}
