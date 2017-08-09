package uk.ac.mib.antismashoops.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.ac.mib.antismashoops.core.datatransferobject.SelfHomologyDTO;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.service.params.SelfHomologyService;
import uk.ac.mib.antismashoops.web.utils.Workspace;

@Slf4j
@Component
public class ScoringService
{
    /**
     * Calls the Self-Homology Score function for each cluster. If the Homology
     * for a determined minimum match paramter is already calculated then it
     * will be cached.
     *
     * @param minimumMatch The minumum contiguous nucleotide strand length in
     *                     order to be considered for the final score.
     */

    public void setSelfHomologyScore(Workspace workspace, ApplicationBgcData appData, int minimumMatch)
    {
        SelfHomologyService.DIRECTORY = workspace.getRoot().getAbsolutePath() + "/selfHomology";

        for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet())
        {
            if (bgc.getSelfHomologyScores().containsKey(minimumMatch))
            {
                bgc.setSelfHomologyScore(bgc.getSelfHomologyScores().get(minimumMatch).getSelfHomologyScore());
                bgc.setSelfHomologyMaximumScore(bgc.getSelfHomologyScores().get(minimumMatch).getMaximumMatchScore());
            }
            else
            {
                log.info("Self-Homology calculation for Cluster " + bgc.getClusterId() + " started...");
                SelfHomologyDTO selfHomologyDTO = SelfHomologyService.calculateScore(bgc.getClusterSequence(), minimumMatch,
                    bgc.getOrigin(), bgc.getNumber());
                bgc.setSelfHomologyScore(selfHomologyDTO.getSelfHomologyScore());
                bgc.setSelfHomologyMaximumScore(selfHomologyDTO.getMaximumMatchScore());
                bgc.getSelfHomologyScores().put(minimumMatch, selfHomologyDTO);
                log.info("Cluster: {} Score: {}", bgc.getName(), bgc.getSelfHomologyScore());
                log.info("...self-Homology calculation finished.");
            }
        }
    }


    /**
     * Generates the LineageTree for each BGC and writes it to the associated
     * files. Sets the diversity score per BGC getting the size of the generated
     * tree of life.
     */

    public void setPhylogeneticDiversityScore(ApplicationBgcData appData)
    {
        for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet())
        {
            bgc.getClusterBlastsData().generateLineageTree();
            bgc.setDiversityScore(bgc.getClusterBlastsData().getDiversityScore());
        }
    }
}
