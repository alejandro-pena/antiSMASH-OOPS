package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import uk.ac.mib.antismashoops.core.service.OnlineResourceService;
import uk.ac.mib.antismashoops.web.utils.Workspace;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Slf4j
@Controller
public class CodonUsageChartController
{
    private ApplicationBgcData appData;
    private OnlineResourceService onlineResourceService;
    private WorkspaceManager workspaceManager;


    @Autowired
    public CodonUsageChartController(ApplicationBgcData appData, OnlineResourceService onlineResourceService, WorkspaceManager workspaceManager)
    {
        this.appData = appData;
        this.onlineResourceService = onlineResourceService;
        this.workspaceManager = workspaceManager;
    }


    /**
     * Handles the URL call to /codonUsageChart specifing a cluster of the
     * application and the species to compare it to. The controller find the
     * cluster in the application data and gets the codon usage data of the
     * species using the Online Resource Service and finally returns the two
     * codon usage tables.
     *
     * @param model       The backing model object for the codonUsageChart View where
     *                    the necessary objects are appended.
     * @param clusterName The cluster requested by the user
     * @param species     The species name
     * @return The codonUsageChart HTML view showing the Cluster Codon Usage
     * Data compared to the Species Codon Usage Data
     * @throws IOException Throws Input Output Exception
     */

    @RequestMapping(value = "/codonUsageChart/{clusterName:.+}/{species:.+}", method = RequestMethod.GET)
    public String getCodonUsageChart(
        ModelMap model, @PathVariable("clusterName") String clusterName,
        @PathVariable("species") String species) throws IOException
    {

        List<BiosyntheticGeneCluster> bgcData = appData.getWorkingDataSet();

        BiosyntheticGeneCluster requested = null;
        for (BiosyntheticGeneCluster c : bgcData)
        {
            if (c.getName().equalsIgnoreCase(clusterName))
            {
                requested = c;
                break;
            }
        }

        log.info("Getting the Codon Usage from the requested species...");
        CodonUsageTable cutRef = onlineResourceService.getSpeciesUsageTable(species);
        CodonUsageTable cutBgc = requested.getCodonUsageTable();

        model.addAttribute("tableRef", cutRef.getUsage());
        model.addAttribute("tableBgc", cutBgc.getUsage());

        log.info("Loading Codon Usage Chart...");

        return "codonUsageChart";
    }


    /**
     * Handles the URL call to /codonUsageMap specifing a cluster of the
     * application and the species to compare it to. The controller find the
     * cluster in the application data and gets the codon usage data of the
     * species using the Online Resource Service and finally returns the two
     * codon usage tables.
     *
     * @param model       The backing model object for the codonUsageChart View where
     *                    the necessary objects are appended.
     * @param clusterName The cluster requested by the user
     * @param species     The species name
     * @return The codonUsageMap HTML view showing the Cluster Codon Usage Data
     * compared to the Species Codon Usage Data
     * @throws IOException Throws Input Output Exception
     */

    @RequestMapping(value = "/codonUsageMap/{clusterName:.+}/{species:.+}", method = RequestMethod.GET)
    public String getCodonUsageMap(
        ModelMap model, @PathVariable("clusterName") String clusterName,
        @PathVariable("species") String species) throws IOException
    {
        Workspace workspace = workspaceManager.getCurrentWorkspace();
        List<BiosyntheticGeneCluster> bgcData;
        if ("antiSMASH_Actinobacterial_BGCs".equals(workspace.getName()))
        {
            bgcData = appData.getPreprocessedBgcData();
        }
        else
        {
            bgcData = appData.getWorkingDataSet();
        }

        BiosyntheticGeneCluster requested = null;
        for (BiosyntheticGeneCluster c : bgcData)
        {
            if (c.getName().equalsIgnoreCase(clusterName))
            {
                requested = c;
                break;
            }
        }

        log.info("Getting the Codon Usage from the requested species...");
        CodonUsageTable cutRef = onlineResourceService.getSpeciesUsageTable(species);
        CodonUsageTable cutBgc = requested.getCodonUsageTable();

        model.addAttribute("tableRef", cutRef.getUsage());
        model.addAttribute("tableBgc", cutBgc.getUsage());

        log.info("Loading Codon Usage Heat Map...");

        return "codonUsageMap";
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }
}
