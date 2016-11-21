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
import uk.ac.mib.antismashoops.core.services.OnlineResourceService;

@Controller
public class SpeciesDropDownController {

	private static final Logger logger = LoggerFactory.getLogger(SpeciesDropDownController.class);

	@Autowired
	OnlineResourceService ors;

	/**
	 * Handles the URL call to /species specifing a species name. The controller
	 * will call to the online resource service and provide the species list
	 * from the Kazusa website.
	 * 
	 * @param model The backing model object for the Dashboard View where the
	 *            necessary objects are appended.
	 * 
	 * @param species The species query name to be searched
	 * 
	 * @return A DropDown list with the species found
	 * 
	 * @throws IOException Throws Input Output Exception
	 */

	@RequestMapping(value = "/species/{species}", method = RequestMethod.GET)
	public String getSpeciesByName(Model model, @PathVariable("species") String species) throws IOException {

		model.addAttribute("speciesList", ors.getSpeciesByName(species));
		return "fragments/speciesDD :: speciesDD";
	}
}
