package uk.ac.mib.antismashoops.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.KnownCluster;

@Scope("singleton")
@Component
public class ScoringService {

	private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);

	@Autowired
	ApplicationBgcData appData;

	/**
	 * 
	 * Sets the Known Cluster Similarity score for each BGC in the application.
	 * It the Best Match Score calling the method from each KnownCluster object
	 * associated to the BGCs
	 * 
	 * @param preferredSimilarity The preferred similarity percentage specified
	 *            by the user
	 */

	public void setKnownClusterSimilarityScore(double preferredSimilarity) {
		for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet()) {
			KnownCluster kc = bgc.getKnownClustersData();
			if (kc != null)
				bgc.setKcScore(kc.getBestMatchScore(preferredSimilarity));
		}
	}

	/**
	 * Calls the Self-Homology Score function for each cluster. If the Homology
	 * for a determined minimum match paramter is already calculated then it
	 * will be cached.
	 *
	 * @param minimumMatch The minumum contiguous nucleotide strand length in
	 *            order to be considered for the final score.
	 */

	public void setSelfHomologyScore(int minimumMatch) {
		for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet()) {
			if (bgc.getSelfHomologyScores().containsKey(minimumMatch))
				bgc.setSelfHomologyScore(bgc.getSelfHomologyScores().get(minimumMatch));
			else {
				logger.info("Self-Homology calculation for Cluster " + bgc.getClusterId() + " started...");
				bgc.setSelfHomologyScore(SelfHomologyService.calculateScore(bgc.getClusterSequence(), minimumMatch,
						bgc.getOrigin(), bgc.getNumber()));
				bgc.getSelfHomologyScores().put(minimumMatch, bgc.getSelfHomologyScore());
				logger.info("...self-Homology calculation finished.");
			}
		}
	}

	/**
	 * 
	 * Generates the LineageTree for each BGC and writes it to the associated
	 * files. Sets the diversity score per BGC getting the size of the generated
	 * tree of life.
	 * 
	 */

	public void setPhylogeneticDiversityScore() {
		for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet()) {
			bgc.getClusterBlastsData().generateLineageTree();
			bgc.setDiversityScore(bgc.getClusterBlastsData().getDiversityScore());
		}
	}
}
