package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastData;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastEntry;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainobject.KnownCluster;
import uk.ac.mib.antismashoops.core.domainobject.KnownClusterData;
import uk.ac.mib.antismashoops.core.domainvalue.ClusterSort;
import uk.ac.mib.antismashoops.core.services.OnlineResourceService;
import uk.ac.mib.antismashoops.core.services.ScoringService;
import uk.ac.mib.antismashoops.core.services.SelfHomologyService;

@Controller
public class DashboardController {
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private ApplicationBgcData appData;

	@Autowired
	private OnlineResourceService ors;

	@Autowired
	private ScoringService scoreService;

	@Autowired
	private ClusterBlastData cbd;

	@Autowired
	private KnownClusterData kcd;

	/**
	 * Responds to the /dashboard URL request coming from the Index Page or a
	 * page reload. Receives no parameters from the request.
	 *
	 * @param model The backing model object for the Dashboard View where the
	 *            necessary objects are appended.
	 * 
	 * @return The Dashboard HTML View to be rendered by Thymeleaf having as
	 *         attributes the BGC Data and the BGC Types List
	 */

	@RequestMapping("/dashboard")
	public String showResults(ModelMap model) throws IOException {

		List<BiosyntheticGeneCluster> bgcData = appData.getBgcData();
		List<String> typesList = this.getClusterTypesList(bgcData);

		logger.info("Dashboard Loaded...");

		model.addAttribute("typesList", typesList);
		model.addAttribute("clusterData", bgcData);

		return "dashboard";
	}

	/**
	 * Responds to the /dashboard URL request with all the parameters for the
	 * prioritisation coming from the AJAX call in the Dashboard View. Receives
	 * 19 parameters which will define the prioritisation engine logic.
	 *
	 * @param geneCount User preference for the parameter
	 * @param nogOrder pdOrder Ascending or Descending (a or d)
	 * @param sequenceLength User preference for the parameter
	 * @param slOrder pdOrder Ascending or Descending (a or d)
	 * @param gcContent User preference for the parameter
	 * @param gccOrder pdOrder Ascending or Descending (a or d)
	 * @param codonBias User preference for the parameter
	 * @param cbOrder pdOrder Ascending or Descending (a or d)
	 * @param refSpecies specified by the user, undefined isn't specified
	 * @param types BGC types selected by the user to show
	 * @param ignorePT Ignore flag for the Preferred type filtering option
	 * @param knownCluster User preference for the parameter
	 * @param kcsOrder pdOrder Ascending or Descending (a or d)
	 * @param preferredSimilarity The preferred percentage of similarity
	 * @param selfHomology User preference for the parameter
	 * @param shOrder pdOrder Ascending or Descending (a or d)
	 * @param minimumMatch The minimum nucleotide match to be considered
	 * @param pDiversity User preference for the parameter
	 * @param pdOrder Ascending or Descending (a or d)
	 * @param model The backing model object for the Dashboard View where the
	 *            necessary objects are appended.
	 * 
	 * @return The Dashboard HTML View to be rendered by Thymeleaf having as
	 *         attributes the BGC Data and the BGC Types List
	 */

	@RequestMapping("/dashboardUpdate")
	public String updateResults(@RequestParam(value = "geneCount", required = true) int geneCount,
			@RequestParam(value = "nogOrderValue", required = true) String nogOrder,
			@RequestParam(value = "sequenceLength", required = true) int sequenceLength,
			@RequestParam(value = "slOrderValue", required = true) String slOrder,
			@RequestParam(value = "gcContent", required = true) int gcContent,
			@RequestParam(value = "gccOrderValue", required = true) String gccOrder,
			@RequestParam(value = "codonBias", required = true) int codonBias,
			@RequestParam(value = "cbOrderValue", required = true) String cbOrder,
			@RequestParam(value = "refSpecies", required = true) String refSpecies,
			@RequestParam(value = "types", required = true) String types,
			@RequestParam(value = "ignorePT", required = true) String ignorePT,
			@RequestParam(value = "kcs", required = true) int knownCluster,
			@RequestParam(value = "kcsOrderValue", required = true) String kcsOrder,
			@RequestParam(value = "pSim", required = true) double preferredSimilarity,
			@RequestParam(value = "sh", required = true) int selfHomology,
			@RequestParam(value = "shOrderValue", required = true) String shOrder,
			@RequestParam(value = "minM", required = true) int minimumMatch,
			@RequestParam(value = "pd", required = true) int pDiversity,
			@RequestParam(value = "pdOrderValue", required = true) String pdOrder, ModelMap model) throws IOException {

		logger.info("Reloading Dashboard...");

		List<BiosyntheticGeneCluster> workingDataSet = appData.getBgcData();

		// FILTER THE BGC DATA ACCORDING TO THE CLUSTER TYPE IF SPECIFIED

		if (ignorePT.equalsIgnoreCase("false") && !types.equals("null")) {

			Set<String> typesSet = this.getTypesSet(types);

			workingDataSet = workingDataSet.stream()
					.filter(bgc -> typesSet.stream().anyMatch(bgc.getClusterTypes()::contains))
					.collect(Collectors.toList());
		}

		// SET THE KNOWN CLUSTER DATA

		List<KnownCluster> kcl = kcd.getKnownClusterData();

		for (KnownCluster kce : kcl) {
			BiosyntheticGeneCluster bgc = getCluster(kce.getClusterId());
			if (bgc != null)
				bgc.setKcScore(kce.getBestMatchScore(preferredSimilarity));
		}

		Double gcContentRef = 0.0;
		CodonUsageTable cutRef;

		// SORT BY ONLY THE BASIC FOUR PARAMETERS WITHOUT A REFERENCE SPECIES

		if (refSpecies.equalsIgnoreCase("undefined")) {

			if (geneCount > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
			if (sequenceLength > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
			if (gcContent > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCSORT : ClusterSort.GCCSORTREV, gcContent);
			if (knownCluster > 0) {
				scoreService.assignScoreForParameter(workingDataSet,
						kcsOrder.equalsIgnoreCase("a") ? ClusterSort.KCSORT : ClusterSort.KCSORTREV, knownCluster);
			}
			if (selfHomology > 0) {
				setSelfHomologyScore(workingDataSet, minimumMatch);
				scoreService.assignScoreForParameter(workingDataSet,
						shOrder.equalsIgnoreCase("d") ? ClusterSort.SHSORT : ClusterSort.SHSORTREV, selfHomology);
			}
			if (pDiversity > 0) {
				// SET THE CLUSTER BLAST DATA
				List<ClusterBlastEntry> cbl = cbd.getClusterBlastData(workingDataSet);

				for (ClusterBlastEntry cbe : cbl) {
					cbe.generateLineageTree();
					BiosyntheticGeneCluster bgc = getCluster(cbe.getClusterId());
					if (bgc != null) {
						bgc.setPdScore(cbe.getDiversityScore());
					}
				}
				scoreService.assignScoreForParameter(workingDataSet,
						pdOrder.equalsIgnoreCase("d") ? ClusterSort.PDSORT : ClusterSort.PDSORTREV, pDiversity);
			}
		}

		// SORT USING ALL PARAMETERS AND WITH A REFERENCE SPECIES
		else {
			gcContentRef = ors.getGcContentBySpecies(refSpecies);
			cutRef = ors.getSpeciesUsageTable(refSpecies);
			appData.loadBgcDataWithSpecies(gcContentRef, cutRef);

			if (geneCount > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);

			if (sequenceLength > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
			if (gcContent > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCREFSORTREV : ClusterSort.GCCREFSORT, gcContent);
			if (codonBias > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						cbOrder.equalsIgnoreCase("a") ? ClusterSort.CBSORT : ClusterSort.CBSORTREV, codonBias);
			if (knownCluster > 0)
				scoreService.assignScoreForParameter(workingDataSet,
						kcsOrder.equalsIgnoreCase("a") ? ClusterSort.KCSORT : ClusterSort.KCSORTREV, knownCluster);
			if (selfHomology > 0) {
				setSelfHomologyScore(workingDataSet, minimumMatch);
				scoreService.assignScoreForParameter(workingDataSet,
						shOrder.equalsIgnoreCase("d") ? ClusterSort.SHSORT : ClusterSort.SHSORTREV, selfHomology);
			}

			if (pDiversity > 0) {
				// SET THE CLUSTER BLAST DATA
				List<ClusterBlastEntry> cbl = cbd.getClusterBlastData(workingDataSet);

				for (ClusterBlastEntry cbe : cbl) {
					cbe.generateLineageTree();
					BiosyntheticGeneCluster bgc = getCluster(cbe.getClusterId());
					if (bgc != null) {
						bgc.setPdScore(cbe.getDiversityScore());
					}
				}
				scoreService.assignScoreForParameter(workingDataSet,
						pdOrder.equalsIgnoreCase("d") ? ClusterSort.PDSORT : ClusterSort.PDSORTREV, pDiversity);
			}
		}

		// SORT THE FINAL SCORE RESULT

		Collections.sort(workingDataSet, ClusterSort.SCORESORT);

		logger.info("Prioritisation successful!");

		model.addAttribute("refSpecies", refSpecies);
		model.addAttribute("clusterData", workingDataSet);

		return "fragments/clusterData :: clusterData";
	}

	/**
	 * Transforms the Cluster Type input data (originally a comma separated
	 * String) into a HashSet Collection
	 *
	 * @param types The comma separated String with the Cluster Types selected
	 * 
	 * @return typesSet A HashSet including all the Cluster Types selected
	 */

	private Set<String> getTypesSet(String types) {
		String[] requiredTypes = types.split(",");
		return new HashSet<String>(Arrays.asList(requiredTypes));
	}

	/**
	 * Calls the Self-Homology Score function. If the Homology for a determined
	 * minimum match paramter is already calculated then it will be cached
	 *
	 * @param types The comma separated String with the Cluster Types selected
	 * 
	 * @return typesSet A HashSet including all the Cluster Types selected
	 */

	private void setSelfHomologyScore(List<BiosyntheticGeneCluster> workingDataSet, int minimumMatch) {
		for (BiosyntheticGeneCluster c : workingDataSet) {
			if (c.getSelfHomologyScores().containsKey(minimumMatch))
				c.setSelfHomologyScore(c.getSelfHomologyScores().get(minimumMatch));
			else {
				c.setSelfHomologyScore(SelfHomologyService.calculateScore(c.getClusterSequence(), minimumMatch,
						c.getOrigin(), c.getNumber()));
				c.getSelfHomologyScores().put(minimumMatch, c.getSelfHomologyScore());
			}
		}
	}

	/**
	 * Creates a list with all the Cluster Types found in the BGC Data so that
	 * it can be shown in the Dashboard Page in the Cluster Type select box.
	 *
	 * @param bgcData The List of the BGC Objects loaded to the application.
	 * @return An ArrayList with all the Cluster Types found in the BGC Data.
	 */

	private List<String> getClusterTypesList(List<BiosyntheticGeneCluster> bgcData) {
		Set<String> types = new TreeSet<>();
		for (BiosyntheticGeneCluster c : bgcData) {
			String[] type = c.getClusterType().split("-");
			for (String t : type)
				types.add(t);
		}
		return new ArrayList<>(types);
	}

	private BiosyntheticGeneCluster getCluster(String clusterId) {
		for (BiosyntheticGeneCluster c : appData.getWorkingDataSet()) {
			if (c.getClusterId().equals(clusterId))
				return c;
		}
		return null;
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
