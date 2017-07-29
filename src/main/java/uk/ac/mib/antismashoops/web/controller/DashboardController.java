package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import uk.ac.mib.antismashoops.core.service.ExternalDataService;
import uk.ac.mib.antismashoops.core.service.OnlineResourceService;
import uk.ac.mib.antismashoops.core.service.PrioritisationService;
import uk.ac.mib.antismashoops.core.service.ScoringService;
import uk.ac.mib.antismashoops.core.service.params.KnownClustersService;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Slf4j
@Controller
public class DashboardController
{
    private final ApplicationBgcData applicationBgcData;
    private final OnlineResourceService onlineResourceService;
    private final ScoringService scoreService;
    private final PrioritisationService prioritisationService;
    private final ExternalDataService externalDataService;
    private final KnownClustersService knownClustersService;
    private final WorkspaceManager workspaceManager;


    @Autowired
    public DashboardController(
        ApplicationBgcData applicationBgcData,
        OnlineResourceService onlineResourceService,
        ScoringService scoreService,
        PrioritisationService prioritisationService,
        ExternalDataService externalDataService,
        KnownClustersService knownClustersService,
        WorkspaceManager workspaceManager)
    {
        this.applicationBgcData = applicationBgcData;
        this.onlineResourceService = onlineResourceService;
        this.scoreService = scoreService;
        this.prioritisationService = prioritisationService;
        this.externalDataService = externalDataService;
        this.knownClustersService = knownClustersService;
        this.workspaceManager = workspaceManager;
    }


    /**
     * Responds to the /dashboard URL request coming from the Index Page or a
     * page reload. Receives no parameters from the request.
     *
     * @param model The backing model object for the Dashboard View where the
     *              necessary objects are appended.
     * @return The Dashboard HTML View to be rendered by Thymeleaf having as
     * attributes the BGC Data and the BGC Types List
     * @throws IOException Throws Input Output Exception
     */

    @RequestMapping("/dashboard")
    public String showDashboard(ModelMap model) throws IOException
    {

        List<BiosyntheticGeneCluster> bgcData = applicationBgcData.getBgcData(workspaceManager.getCurrentWorkspace());
        List<String> typesList = this.getClusterTypesList(bgcData);

        log.info("Dashboard Loaded...");

        model.addAttribute("typesList", typesList);
        model.addAttribute("clusterData", bgcData);
        model.addAttribute("wsName", workspaceManager.getCurrentWorkspace().getName());

        return "dashboard";
    }


    /**
     * Responds to the /dashboard URL request with all the parameters for the
     * prioritisation coming from the AJAX call in the Dashboard View. Receives
     * 19 parameters which will define the prioritisation engine logic.
     *
     * @param geneCount                           User preference for the parameter
     * @param nogOrder                            pdOrder Ascending or Descending (a or d)
     * @param sequenceLength                      User preference for the parameter
     * @param slOrder                             pdOrder Ascending or Descending (a or d)
     * @param gcContent                           User preference for the parameter
     * @param gccOrder                            pdOrder Ascending or Descending (a or d)
     * @param codonBias                           User preference for the parameter
     * @param cbOrder                             pdOrder Ascending or Descending (a or d)
     * @param refSpecies                          specified by the user, undefined isn't specified
     * @param types                               BGC types selected by the user to show
     * @param ignorePT                            Ignore flag for the Preferred type filtering option
     * @param knownCluster                        User preference for the parameter
     * @param kcsOrder                            pdOrder Ascending or Descending (a or d)
     * @param preferrexternalDataServiceimilarity The preferred percentage of similarity
     * @param selfHomology                        User preference for the parameter
     * @param shOrder                             pdOrder Ascending or Descending (a or d)
     * @param minimumMatch                        The minimum nucleotide match to be considered
     * @param pDiversity                          User preference for the parameter
     * @param pdOrder                             Ascending or Descending (a or d)
     * @param model                               The backing model object for the Dashboard View where the
     *                                            necessary objects are appended.
     * @return The Dashboard HTML View to be rendered by Thymeleaf having as
     * attributes the BGC Data and the BGC Types List
     * @throws IOException Throws Input Output Exception
     */

    @RequestMapping("/dashboardUpdate")
    public String updateResults(
        @RequestParam(value = "nog") int geneCount,
        @RequestParam(value = "nogo") String nogOrder,
        @RequestParam(value = "sl") int sequenceLength,
        @RequestParam(value = "slo") String slOrder,
        @RequestParam(value = "gcc") int gcContent,
        @RequestParam(value = "gcco") String gccOrder,
        @RequestParam(value = "cb") int codonBias,
        @RequestParam(value = "cbo") String cbOrder,
        @RequestParam(value = "rs") String refSpecies,
        @RequestParam(value = "t[]", required = false) Set<String> types,
        @RequestParam(value = "ipt") String ignorePT,
        @RequestParam(value = "kcs") int knownCluster,
        @RequestParam(value = "kcso") String kcsOrder,
        @RequestParam(value = "psim") double preferrexternalDataServiceimilarity,
        @RequestParam(value = "sh") int selfHomology,
        @RequestParam(value = "sho") String shOrder,
        @RequestParam(value = "minm") int minimumMatch,
        @RequestParam(value = "pd") int pDiversity,
        @RequestParam(value = "pdo") String pdOrder, ModelMap model) throws IOException
    {

        log.info("Reloading Dashboard...");

        List<BiosyntheticGeneCluster> workingDataSet = applicationBgcData.getBgcData(workspaceManager.getCurrentWorkspace());

        // FILTER THE BGC DATA ACCORDING TO THE CLUSTER TYPE IF SPECIFIED

        if (ignorePT.equalsIgnoreCase("false") && types != null)
        {
            applicationBgcData.setWorkingDataSet(
                workingDataSet.stream().filter(bgc -> types.stream().anyMatch(bgc.getClusterTypes()::contains))
                    .collect(Collectors.toList()));
            workingDataSet = applicationBgcData.getWorkingDataSet();
        }

        // SET THE KNOWN CLUSTER DATA

        knownClustersService.setKnownClusterData(applicationBgcData, workspaceManager.getCurrentWorkspace());
        knownClustersService.setKnownClusterSimilarityScore(applicationBgcData, preferrexternalDataServiceimilarity);

        // SORT BY ONLY THE BASIC FOUR PARAMETERS WITHOUT A REFERENCE SPECIES

        if (refSpecies.equalsIgnoreCase("undefined"))
        {

            if (geneCount > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
            }
            if (sequenceLength > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
            }
            if (gcContent > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCSORT : ClusterSort.GCCSORTREV, gcContent);
            }
            if (knownCluster > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    kcsOrder.equalsIgnoreCase("a") ? ClusterSort.KCSORT : ClusterSort.KCSORTREV, knownCluster);
            }
            if (selfHomology > 0)
            {
                scoreService.setSelfHomologyScore(minimumMatch);
                prioritisationService.prioritiseParameterAndAddScore(
                    shOrder.equalsIgnoreCase("d") ? ClusterSort.SHSORT : ClusterSort.SHSORTREV, selfHomology);
            }
            if (pDiversity > 0)
            {
                // SET THE CLUSTER BLAST DATA
                externalDataService.setClusterBlastData(applicationBgcData, workspaceManager.getCurrentWorkspace());
                scoreService.setPhylogeneticDiversityScore();

                prioritisationService.prioritiseParameterAndAddScore(
                    pdOrder.equalsIgnoreCase("d") ? ClusterSort.PDSORT : ClusterSort.PDSORTREV, pDiversity);
            }

        }

        // SORT USING ALL PARAMETERS AND WITH A REFERENCE SPECIES
        else
        {
            Double gcContentRef = onlineResourceService.getGcContentBySpecies(refSpecies);
            CodonUsageTable cutRef = onlineResourceService.getSpeciesUsageTable(refSpecies);
            applicationBgcData.loadBgcDataWithSpecies(gcContentRef, cutRef);

            if (geneCount > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    nogOrder.equalsIgnoreCase("d") ? ClusterSort.NOGSORT : ClusterSort.NOGSORTREV, geneCount);
            }
            if (sequenceLength > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    slOrder.equalsIgnoreCase("d") ? ClusterSort.SLSORT : ClusterSort.SLSORTREV, sequenceLength);
            }
            if (gcContent > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    gccOrder.equalsIgnoreCase("d") ? ClusterSort.GCCREFSORTREV : ClusterSort.GCCREFSORT, gcContent);
            }
            if (codonBias > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    cbOrder.equalsIgnoreCase("a") ? ClusterSort.CBSORT : ClusterSort.CBSORTREV, codonBias);
            }
            if (knownCluster > 0)
            {
                prioritisationService.prioritiseParameterAndAddScore(
                    kcsOrder.equalsIgnoreCase("a") ? ClusterSort.KCSORT : ClusterSort.KCSORTREV, knownCluster);
            }
            if (selfHomology > 0)
            {
                scoreService.setSelfHomologyScore(minimumMatch);
                prioritisationService.prioritiseParameterAndAddScore(
                    shOrder.equalsIgnoreCase("d") ? ClusterSort.SHSORT : ClusterSort.SHSORTREV, selfHomology);
            }
            if (pDiversity > 0)
            {
                // SET THE CLUSTER BLAST DATA
                externalDataService.setClusterBlastData(applicationBgcData, workspaceManager.getCurrentWorkspace());
                scoreService.setPhylogeneticDiversityScore();

                prioritisationService.prioritiseParameterAndAddScore(
                    pdOrder.equalsIgnoreCase("d") ? ClusterSort.PDSORT : ClusterSort.PDSORTREV, pDiversity);
            }
        }

        // SORT THE FINAL SCORE RESULT

        Collections.sort(workingDataSet, ClusterSort.SCORESORT);

        log.info("Prioritisation successful!");

        model.addAttribute("refSpecies", refSpecies);
        model.addAttribute("clusterData", workingDataSet);
        model.addAttribute("wsName", workspaceManager.getCurrentWorkspace().getName());

        return "fragments/clusterData :: clusterData";
    }


    /**
     * Creates a list with all the Cluster Types found in the BGC Data so that
     * it can be shown in the Dashboard Page in the Cluster Type select box.
     *
     * @param bgcData The List of the BGC Objects loaded to the application.
     * @return An ArrayList with all the Cluster Types found in the BGC Data.
     */

    private List<String> getClusterTypesList(List<BiosyntheticGeneCluster> bgcData)
    {
        Set<String> types = new TreeSet<>();
        for (BiosyntheticGeneCluster c : bgcData)
        {
            String[] type = c.getClusterType().split("-");
            for (String t : type)
            {
                types.add(t.trim());
            }
        }
        return new ArrayList<>(types);
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }
}
