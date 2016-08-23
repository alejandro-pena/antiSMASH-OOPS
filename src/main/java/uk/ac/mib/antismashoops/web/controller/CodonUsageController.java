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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainobject.Species;
import uk.ac.mib.antismashoops.core.services.OnlineResourceService;

@Controller
public class CodonUsageController {

	private static final Logger logger = LoggerFactory.getLogger(CodonUsageController.class);

	@Autowired
	OnlineResourceService ors;

	@RequestMapping("/codonUsage")
	public String getSpecies() {
		return "codonUsage";
	}

	@RequestMapping(value = "/codonUsage/{species}", method = RequestMethod.GET)
	public String getSpeciesByName(Model model, @PathVariable("species") String species) throws IOException {
		List<Species> speciesList = new ArrayList<>();

		Document doc = Jsoup.connect("http://www.kazusa.or.jp/codon/cgi-bin/spsearch.cgi?species=" + species + "&c=i")
				.timeout(15000).get();

		Elements links = doc.select("a[href]");

		if (links.size() == 1) {
			speciesList.add(new Species("No data found.", "No data found.", "No data found."));
			model.addAttribute("speciesList", speciesList);
			return "fragments/speciesList :: speciesList";

		}

		links.remove(links.size() - 1);

		for (Element link : links) {
			String id = link.attr("href").split("=")[1];
			speciesList.add(new Species(id, link.text(), "/codonUsage/show/" + id));
		}

		model.addAttribute("speciesList", speciesList);
		return "fragments/speciesList :: speciesList";
	}

	@RequestMapping(value = "/codonUsage/show/{species:.+}", method = RequestMethod.GET)
	public String getSpeciesUsageTable(Model model, @PathVariable("species") String species) throws IOException {

		CodonUsageTable cu = ors.getSpeciesUsageTable(species);
		model.addAttribute("table", cu.getUsage());
		return "codonUsageTable";
	}
}
