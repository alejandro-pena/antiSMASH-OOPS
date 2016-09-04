package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.services.OnlineResourceService;

@Controller
public class CodonUsageController {

	private static final Logger logger = LoggerFactory.getLogger(CodonUsageController.class);

	@Autowired
	OnlineResourceService ors;

	/**
	 * Handles the URL call to /codonUsage
	 * 
	 * @return The codonUsage HTML view
	 */

	@RequestMapping("/codonUsage")
	public String getSpecies() {
		return "codonUsage";
	}

	/**
	 * Handles the URL call to /codonUsage specifing a species to retrieve the
	 * Codon Usage Data from.
	 * 
	 * @param model The backing model object for the speciesList fragment where
	 *            the necessary objects are appended.
	 * 
	 * @param species The species name
	 * 
	 * @return The speciesList HTML fragment to be displayed in the results
	 *         section.
	 * 
	 * @throws IOException Throws Input Output Exception
	 */

	@RequestMapping(value = "/codonUsage/{species}", method = RequestMethod.GET)
	public String getSpeciesByName(Model model, @PathVariable("species") String species) throws IOException {

		model.addAttribute("speciesList", ors.getSpeciesByNameForCodonUsage(species));
		return "fragments/speciesList :: speciesList";
	}

	/**
	 * Handles the URL call to /codonUsage/show specifing a species to retrieve
	 * the Codon Usage Data from.
	 * 
	 * @param model The backing model object for the codonUsageTable view where
	 *            the necessary objects are appended.
	 * 
	 * @param species The species requested
	 * 
	 * @return The codonUsageTable HTML view showing the Species Codon Usage
	 *         Data
	 * 
	 * @throws IOException Throws Input Output Exception
	 */

	@RequestMapping(value = "/codonUsage/show/{species:.+}", method = RequestMethod.GET)
	public String getSpeciesUsageTable(Model model, @PathVariable("species") String species) throws IOException {

		CodonUsageTable cu = ors.getSpeciesUsageTable(species);
		model.addAttribute("table", cu.getUsage());
		return "codonUsageTable";
	}
}
