package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.services.OnlineResourceService;

@Controller
public class CodonUsageChartController {
	private static final Logger logger = LoggerFactory.getLogger(CodonUsageChartController.class);

	@Autowired
	ApplicationBgcData appData;

	@Autowired
	OnlineResourceService ors;

	@RequestMapping(value = "/codonUsageChart/{clusterName:.+}/{species:.+}", method = RequestMethod.GET)
	public String getCodonUsageChart(ModelMap model, @PathVariable("clusterName") String clusterName,
			@PathVariable("species") String species) throws IOException {

		List<BiosyntheticGeneCluster> bgcData = appData.getBgcData();

		BiosyntheticGeneCluster requested = null;
		for (BiosyntheticGeneCluster c : bgcData) {
			if (c.getName().equalsIgnoreCase(clusterName)) {
				requested = c;
				break;
			}
		}

		logger.info("Getting the Codon Usage from the requested species...");
		CodonUsageTable cutRef = ors.getSpeciesUsageTable(species);
		CodonUsageTable cutBgc = requested.getCodonUsageTable();

		model.addAttribute("tableRef", cutRef.getUsage());
		model.addAttribute("tableBgc", cutBgc.getUsage());

		logger.info("Loading Codon Usage Chart...");

		return "codonUsageChart";
	}

	@RequestMapping(value = "/codonUsageMap/{clusterName:.+}/{species:.+}", method = RequestMethod.GET)
	public String getCodonUsageMap(ModelMap model, @PathVariable("clusterName") String clusterName,
			@PathVariable("species") String species) throws IOException {

		List<BiosyntheticGeneCluster> bgcData = appData.getBgcData();

		BiosyntheticGeneCluster requested = null;
		for (BiosyntheticGeneCluster c : bgcData) {
			if (c.getName().equalsIgnoreCase(clusterName)) {
				requested = c;
				break;
			}
		}

		logger.info("Getting the Codon Usage from the requested species...");
		CodonUsageTable cutRef = ors.getSpeciesUsageTable(species);
		CodonUsageTable cutBgc = requested.getCodonUsageTable();

		model.addAttribute("tableRef", cutRef.getUsage());
		model.addAttribute("tableBgc", cutBgc.getUsage());

		logger.info("Loading Codon Usage Heat Map...");

		return "codonUsageMap";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		req.setAttribute("error", exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
