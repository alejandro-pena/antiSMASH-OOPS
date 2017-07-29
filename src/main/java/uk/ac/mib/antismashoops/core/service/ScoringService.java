package uk.ac.mib.antismashoops.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

@Slf4j
@Component
public class ScoringService
{

    @Autowired
    ApplicationBgcData appData;


    /**
     * Calls the Self-Homology Score function for each cluster. If the Homology
     * for a determined minimum match paramter is already calculated then it
     * will be cached.
     *
     * @param minimumMatch The minumum contiguous nucleotide strand length in
     *                     order to be considered for the final score.
     */

    public void setSelfHomologyScore(int minimumMatch)
    {
        for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet())
        {
            if (bgc.getSelfHomologyScores().containsKey(minimumMatch))
            {
                bgc.setSelfHomologyScore(bgc.getSelfHomologyScores().get(minimumMatch));
            }
            else
            {
                log.info("Self-Homology calculation for Cluster " + bgc.getClusterId() + " started...");
                bgc.setSelfHomologyScore(SelfHomologyService.calculateScore(bgc.getClusterSequence(), minimumMatch,
                    bgc.getOrigin(), bgc.getNumber()));
                bgc.getSelfHomologyScores().put(minimumMatch, bgc.getSelfHomologyScore());
                log.info("...self-Homology calculation finished.");
            }
        }
    }


    /**
     * Generates the LineageTree for each BGC and writes it to the associated
     * files. Sets the diversity score per BGC getting the size of the generated
     * tree of life.
     */

    public void setPhylogeneticDiversityScore()
    {
        for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet())
        {
            bgc.getClusterBlastsData().generateLineageTree();
            bgc.setDiversityScore(bgc.getClusterBlastsData().getDiversityScore());
        }
    }
}
