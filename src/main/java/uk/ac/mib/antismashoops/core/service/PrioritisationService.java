package uk.ac.mib.antismashoops.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainvalue.ClusterSort;
import uk.ac.mib.antismashoops.web.utils.Workspace;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Component
public class PrioritisationService
{
    private ApplicationBgcData appData;
    private WorkspaceManager workspaceManager;


    @Autowired
    public PrioritisationService(ApplicationBgcData appData, WorkspaceManager workspaceManager)
    {
        this.appData = appData;
        this.workspaceManager = workspaceManager;
    }


    /**
     * Prioritises the cluster list in a new ArrayList and adds a ponderated
     * score according to the parameter specified to the global final score.
     *
     * @param comparator      Comparator object to understand what type of
     *                        prioritisation is going to be done
     * @param parameterWeight The parameter ponderation specified by the user
     */

    public void prioritiseParameterAndAddScore(ClusterSort comparator, int parameterWeight)
    {
        Workspace workspace = workspaceManager.getCurrentWorkspace();
        List<BiosyntheticGeneCluster> sortedData;

        if ("antiSMASH_Actinobacterial_BGCs".equals(workspace.getName()))
        {
            sortedData = new ArrayList<>(appData.getPreprocessedWorkingDataSet());
            Collections.sort(sortedData, comparator);
            appData.getPreprocessedWorkingDataSet().forEach(bgc -> {
                if (bgc.getSelfHomologyScore() > 0 || (comparator != ClusterSort.SHSORT && comparator != ClusterSort.SHSORTREV))
                {
                    double score = bgc.getScore();
                    bgc.setScore(score += ((sortedData.indexOf(bgc) + 1) * 1.0 * parameterWeight));
                }
            });
        }
        else
        {
            sortedData = new ArrayList<>(appData.getWorkingDataSet());
            Collections.sort(sortedData, comparator);
            appData.getWorkingDataSet().forEach(bgc -> {
                if (bgc.getSelfHomologyScore() > 0 || (comparator != ClusterSort.SHSORT && comparator != ClusterSort.SHSORTREV))
                {
                    double score = bgc.getScore();
                    bgc.setScore(score += ((sortedData.indexOf(bgc) + 1) * 1.0 * parameterWeight));
                }
            });
        }
    }
}
