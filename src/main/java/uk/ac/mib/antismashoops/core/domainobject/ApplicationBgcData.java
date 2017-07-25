package uk.ac.mib.antismashoops.core.domainobject;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.mib.antismashoops.core.service.ExternalDataService;
import uk.ac.mib.antismashoops.web.utils.Workspace;

@Slf4j
@Component
public class ApplicationBgcData
{
    private ExternalDataService externalDataService;

    private final List<BiosyntheticGeneCluster> bgcData;

    @Getter
    @Setter
    private List<BiosyntheticGeneCluster> workingDataSet;


    @Autowired
    public ApplicationBgcData(ExternalDataService externalDataService)
    {
        this.externalDataService = externalDataService;
        this.bgcData = new ArrayList<>();
        this.workingDataSet = new ArrayList<>();
    }


    /**
     * Provides the BGC Application Data, if the data is in sync with the files
     * uploaded the cached version will be provided. If not, a new scan and
     * decompression will be done loading the new files or deleting the erased
     * ones. This method clones the Application Data into the workingDataSet
     * object to keep an intact copy of the data. The workingDataSet is a
     * mutable object that changes according to every prioritisation request.
     *
     * @return The workingDataSet cloned from the original data
     */

    public List<BiosyntheticGeneCluster> getBgcData(Workspace workspace)
    {
        if (externalDataService.isBgcDataInSync(bgcData.size(), workspace))
        {
            this.workingDataSet.clear();
            bgcData.forEach(biosyntheticGeneCluster -> this.workingDataSet.add(biosyntheticGeneCluster.clone()));
            log.info("Cluster Data in sync...");
            return workingDataSet;
        }

        log.info("Syncing Cluster Data...");
        externalDataService.decompressLoadedFiles(workspace);
        externalDataService.loadBggData(bgcData, workspace);
        this.workingDataSet.clear();
        bgcData.forEach(biosyntheticGeneCluster -> this.workingDataSet.add(biosyntheticGeneCluster.clone()));
        return workingDataSet;
    }


    /**
     * Loads the BGC Data scores of the GC Content using a reference species and
     * the Codon Bias
     *
     * @param gcContentRef The Reference Species GC Content Percentage
     * @param cutRef       The CodonUsageTable object with the reference species data
     */

    public void loadBgcDataWithSpecies(Double gcContentRef, CodonUsageTable cutRef)
    {
        externalDataService.populateClusterData(workingDataSet, gcContentRef, cutRef);
    }


    /**
     * Finds a specific BGC Object by the Cluster Id specified.
     *
     * @param clusterId The BGC Id to look for in the workingDataSet
     * @return The Biosynthetic Gene Cluster requested
     */

    public BiosyntheticGeneCluster getCluster(String clusterId)
    {
        for (BiosyntheticGeneCluster c : workingDataSet)
        {
            if (c.getClusterId().equals(clusterId))
            {
                return c;
            }
        }
        return null;
    }
}
