package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.web.utils.Workspace;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Slf4j
@Controller
public class CodonTablesController
{
    private ApplicationBgcData appData;
    private WorkspaceManager workspaceManager;


    @Autowired
    public CodonTablesController(ApplicationBgcData appData, WorkspaceManager workspaceManager)
    {
        this.appData = appData;
        this.workspaceManager = workspaceManager;
    }


    /**
     * Handles the URL call to /codonTable specifing a cluster of the
     * application. The controller find the cluster in the application data and
     * return in the model the codon usage table.
     *
     * @param model   The backing model object for the codonUsageTable View where
     *                the necessary objects are appended.
     * @param cluster The cluster requested by the user
     * @return The codonUsageTable HTML view showing the Cluster Codon Usage
     * Data.
     * @throws IOException Throws Input Output Exception
     */

    @GetMapping(value = "/codonTable/{cluster:.+}")
    public String getCodonUsageInfo(Model model, @PathVariable("cluster") String cluster) throws IOException
    {
        Workspace workspace = workspaceManager.getCurrentWorkspace();
        List<BiosyntheticGeneCluster> clusterData;
        if ("antiSMASH_Actinobacterial_BGCs".equals(workspace.getName()))
        {
            clusterData = appData.getPreprocessedBgcData();
        }
        else
        {
            clusterData = appData.getWorkingDataSet();
        }

        BiosyntheticGeneCluster requested = null;
        for (BiosyntheticGeneCluster c : clusterData)
        {
            if (c.getName().equalsIgnoreCase(cluster))
            {
                requested = c;
                break;
            }
        }

        log.info("Loading Codon Usage Table for Cluster: " + requested.getName());
        requested.computeCodonUsageTable();

        model.addAttribute("name", requested.getOrigin() + " - Cluster" + requested.getNumber());
        model.addAttribute("table", requested.getCodonUsageTable().getUsage());

        return "codonUsageTable";
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }
}
