package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.service.OnlineResourceService;

@Controller
@Slf4j
public class CodonUsageController
{

    private final OnlineResourceService onlineResourceService;


    public CodonUsageController(OnlineResourceService onlineResourceService)
    {
        this.onlineResourceService = onlineResourceService;
    }


    /**
     * Handles the URL call to /codonUsage
     *
     * @return The codonUsage HTML view
     */

    @RequestMapping("/codonUsage")
    public String getSpecies()
    {
        return "codonUsage";
    }


    /**
     * Handles the URL call to /codonUsage specifing a species to retrieve the
     * Codon Usage Data from.
     *
     * @param model   The backing model object for the speciesList fragment where
     *                the necessary objects are appended.
     * @param species The species name
     * @return The speciesList HTML fragment to be displayed in the results
     * section.
     * @throws IOException Throws Input Output Exception
     */

    @GetMapping(value = "/codonUsage/{species}")
    public String getSpeciesByName(Model model, @PathVariable("species") String species) throws IOException
    {

        model.addAttribute("speciesList", onlineResourceService.getSpeciesByNameForCodonUsage(species));
        return "fragments/speciesList :: speciesList";
    }


    /**
     * Handles the URL call to /codonUsage/show specifing a species to retrieve
     * the Codon Usage Data from.
     *
     * @param model   The backing model object for the codonUsageTable view where
     *                the necessary objects are appended.
     * @param species The species requested
     * @return The codonUsageTable HTML view showing the Species Codon Usage
     * Data
     * @throws IOException Throws Input Output Exception
     */

    @GetMapping(value = "/codonUsage/show/{species:.+}")
    public String getSpeciesUsageTable(Model model, @PathVariable("species") String species) throws IOException
    {

        CodonUsageTable cu = onlineResourceService.getSpeciesUsageTable(species);
        model.addAttribute("table", cu.getUsage());
        return "codonUsageTable";
    }
}
