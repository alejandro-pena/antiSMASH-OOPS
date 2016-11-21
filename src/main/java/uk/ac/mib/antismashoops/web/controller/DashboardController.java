package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainvalue.ClusterSort;
import uk.ac.mib.antismashoops.core.services.ExternalDataService;
import uk.ac.mib.antismashoops.core.services.OnlineResourceService;
import uk.ac.mib.antismashoops.core.services.PrioritisationService;
import uk.ac.mib.antismashoops.core.services.ScoringService;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Controller
public class DashboardController {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    private ApplicationBgcData appData;
	private OnlineResourceService ors;
	private ScoringService scoreService;
	private PrioritisationService prioritisationService;
    private ExternalDataService eds;
    private WorkspaceManager wm;

	@Autowired
    public DashboardController(
        ApplicationBgcData appData, OnlineResourceService ors, ScoringService scoreService,
        PrioritisationService prioritisationService, ExternalDataService eds, WorkspaceManager wm)
    {
        this.appData = appData;
        this.ors = ors;
        this.scoreService = scoreService;
        this.prioritisationService = prioritisationService;
        this.eds = eds;
        this.wm = wm;
    }

	/**
	 * Responds to the /dashboard URL request coming from the Index Page or a
	 * page reload. Receives no parameters from the request.
	 *
     * @param model
     *            The backing model object for the Dashboard View where the
     *            necessary objects are appended.
	 * 
	 * @return The Dashboard HTML View to be rendered by Thymeleaf having as
	 *         attributes the BGC Data and the BGC Types List
	 *
     * @throws IOException
     *             Throws Input Output Exception
     */

	@RequestMapping("/dashboard")
	public String showResults(ModelMap model) throws IOException {

		List<BiosyntheticGeneCluster> bgcData = appData.getBgcData();
		List<String> typesList = this.getClusterTypesList(bgcData);

        LOG.info("Dashboard Loaded...");

		model.addAttribute("typesList", typesList);
		model.addAttribute("clusterData", bgcData);
        model.addAttribute("wsName", wm.getCurrentWorkspace().getName());

		return "dashboard";
	}

	/**
	 * Responds to the /dashboard URL request with all the parameters for the
	 * prioritisation coming from the AJAX call in the Dashboard View. Receives
	 * 19 parameters which will define the prioritisation engine logic.
	 *
     * @param geneCount
     *            User preference for the parameter
     * @param nogOrder
     *            pdOrder Ascending or Descending (a or d)
     * @param sequenceLength
     *            User preference for the parameter
     * @param slOrder
     *            pdOrder Ascending or Descending (a or d)
     * @param gcContent
     *            User preference for the parameter
     * @param gccOrder
     *            pdOrder Ascending or Descending (a or d)
     * @param codonBias
     *            User preference for the parameter
     * @param cbOrder
     *            pdOrder Ascending or Descending (a or d)
     * @param refSpecies
     *            specified by the user, undefined isn't specified
     * @param types
     *            BGC types selected by the user to show
     * @param ignorePT
     *            Ignore flag for the Preferred type filtering option
     * @param knownCluster
     *            User preference for the parameter
     * @param kcsOrder
     *            pdOrder Ascending or Descending (a or d)
     * @param preferredSimilarity
     *            The preferred percentage of similarity
     * @param selfHomology
     *            User preference for the parameter
     * @param shOrder
     *            pdOrder Ascending or Descending (a or d)
     * @param minimumMatch
     *            The minimum nucleotide match to be considered
     * @param pDiversity
     *            User preference for the parameter
     * @param pdOrder
     *            Ascending or Descending (a or d)
     * @param model
     *            The backing model object for the Dashboard View where the
     *            necessary objects are appended.
	 * 
	 * @return The Dashboard HTML View to be rendered by Thymeleaf having as
	 *         attributes the BGC Data and the BGC Types List
	 *
     * @throws IOException
     *             Throws Input Output Exception
     */

	@RequestMapping("/dashboardUpdate")
	public String updateResults(@RequestParam(value = "nog", required = true) int geneCount,
			@RequestParam(value = "nogo", required = true) String nogOrder,
			@RequestParam(value = "sl", required = true) int sequenceLength,
			@RequestParam(value = "slo", required = true) String slOrder,
			@RequestParam(value = "gcc", required = true) int gcContent,
			@RequestParam(value = "gcco", required = true) String gccOrder,
			@RequestParam(value = "cb", required = true) int codonBias,
			@RequestParam(value = "cbo", required = true) String cbOrder,
			@RequestParam(value = "rs", required = true) String refSpecies,
			@RequestParam(value = "t[]", required = false) Set<String> types,
			@RequestParam(value = "ipt", required = true) String ignorePT,
			@RequestParam(value = "kcs", required = true) int knownCluster,
			@RequestParam(value = "kcso", required = true) String kcsOrder,
			@RequestParam(value = "psim", required = true) double preferredSimilarity,
			@RequestParam(value = "sh", required = true) int selfHomology,
			@RequestParam(value = "sho", required = true) String shOrder,
			@RequestParam(value = "minm", required = true) int minimumMatch,
			@RequestParam(value = "pd", required = true) int pDiversity,
			@RequestParam(value = "pdo", required = true) String pdOrder, ModelMap model) throws IOException {

        LOG.info("Reloading Dashboard...");

		List<BiosyntheticGeneCluster> workingDataSet = appData.getBgcData();

		// FILTER THE BGC DATA ACCORDING TO THE CLUSTER TYPE IF SPECIFIED

		if (ignorePT.equalsIgnoreCase("false") && types != null) {

			appData.setWorkingDataSet(
					workingDataSet.stream().filter(bgc -> types.stream().anyMatch(bgc.getClusterTypes()::contains))
							.collect(Collectors.toList()));
			workingDataSet = appData.getWorkingDataSet();
		}

		// SET THE KNOWN CLUSTER DATA

		eds.setKnownClusterData();
		scoreService.setKnownClusterSimilarityScore(preferredSimilarity);

		// SORT BY ONLY THE BASIC FOUR PARAMETERS WITHOUT A REFERENCE SPECIES

		if (refSpecies.equalsIgnoreCase("undefined")) {

			if (geneCount > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
			if (sequenceLength > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
			if (gcContent > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCSORT : ClusterSort.GCCSORTREV, gcContent);
			if (knownCluster > 0) {
				prioritisationService.prioritiseParameterAndAddScore(
						kcsOrder.equalsIgnoreCase("a") ? ClusterSort.KCSORT : ClusterSort.KCSORTREV, knownCluster);
			}
			if (selfHomology > 0) {
				scoreService.setSelfHomologyScore(minimumMatch);
				prioritisationService.prioritiseParameterAndAddScore(
						shOrder.equalsIgnoreCase("d") ? ClusterSort.SHSORT : ClusterSort.SHSORTREV, selfHomology);
			}
			if (pDiversity > 0) {
				// SET THE CLUSTER BLAST DATA
				eds.setClusterBlastData();
				scoreService.setPhylogeneticDiversityScore();

				prioritisationService.prioritiseParameterAndAddScore(
						pdOrder.equalsIgnoreCase("d") ? ClusterSort.PDSORT : ClusterSort.PDSORTREV, pDiversity);
			}

		}

		// SORT USING ALL PARAMETERS AND WITH A REFERENCE SPECIES
		else {
			Double gcContentRef = ors.getGcContentBySpecies(refSpecies);
			CodonUsageTable cutRef = ors.getSpeciesUsageTable(refSpecies);
			appData.loadBgcDataWithSpecies(gcContentRef, cutRef);

			if (geneCount > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
			if (sequenceLength > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
			if (gcContent > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCREFSORTREV : ClusterSort.GCCREFSORT, gcContent);
			if (codonBias > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						cbOrder.equalsIgnoreCase("a") ? ClusterSort.CBSORT : ClusterSort.CBSORTREV, codonBias);
			if (knownCluster > 0)
				prioritisationService.prioritiseParameterAndAddScore(
						kcsOrder.equalsIgnoreCase("a") ? ClusterSort.KCSORT : ClusterSort.KCSORTREV, knownCluster);
			if (selfHomology > 0) {
				scoreService.setSelfHomologyScore(minimumMatch);
				prioritisationService.prioritiseParameterAndAddScore(
						shOrder.equalsIgnoreCase("d") ? ClusterSort.SHSORT : ClusterSort.SHSORTREV, selfHomology);
			}
			if (pDiversity > 0) {
				// SET THE CLUSTER BLAST DATA
				eds.setClusterBlastData();
				scoreService.setPhylogeneticDiversityScore();

				prioritisationService.prioritiseParameterAndAddScore(
						pdOrder.equalsIgnoreCase("d") ? ClusterSort.PDSORT : ClusterSort.PDSORTREV, pDiversity);
			}
		}

		// SORT THE FINAL SCORE RESULT

		Collections.sort(workingDataSet, ClusterSort.SCORESORT);

        LOG.info("Prioritisation successful!");

		model.addAttribute("refSpecies", refSpecies);
		model.addAttribute("clusterData", workingDataSet);
        model.addAttribute("wsName", wm.getCurrentWorkspace().getName());

		return "fragments/clusterData :: clusterData";
	}

	/**
	 * Creates a list with all the Cluster Types found in the BGC Data so that
	 * it can be shown in the Dashboard Page in the Cluster Type select box.
	 *
     * @param bgcData
     *            The List of the BGC Objects loaded to the application.
     * @return An ArrayList with all the Cluster Types found in the BGC Data.
	 */

	private List<String> getClusterTypesList(List<BiosyntheticGeneCluster> bgcData) {
		Set<String> types = new TreeSet<>();
		for (BiosyntheticGeneCluster c : bgcData) {
			String[] type = c.getClusterType().split("-");
			for (String t : type)
				types.add(t.trim());
		}
		return new ArrayList<>(types);
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
        LOG.error("Exception thrown: " + exception.getClass());
        LOG.error("Exception message: " + exception.getMessage());
        exception.printStackTrace();
		return "error";
	}
}
