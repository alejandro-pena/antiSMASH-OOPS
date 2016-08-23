package uk.ac.mib.antismashoops.core.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainvalue.ClusterSort;

@Scope("singleton")
@Component
public class ScoringService {

	public void setGcContentScore(BiosyntheticGeneCluster bgc) {

	}

	public void setCodonBiasScore(BiosyntheticGeneCluster bgc) {

	}

	public void setKnownClusterSimilarityScore(BiosyntheticGeneCluster bgc) {

	}

	public void setSelfHomologyScore(BiosyntheticGeneCluster bgc) {

	}

	public void setPhylogeneticDiversityScore(BiosyntheticGeneCluster bgc) {

	}

	public void assignScoreForParameter(List<BiosyntheticGeneCluster> workingDataSet, ClusterSort comparator,
			int parameterWeight) {

		List<BiosyntheticGeneCluster> sortedData = new ArrayList<>(workingDataSet);
		Collections.sort(sortedData, comparator);

		for (BiosyntheticGeneCluster c : workingDataSet) {
			double score = c.getScore();
			c.setScore(score += ((sortedData.indexOf(c) + 1) * 1.0 * parameterWeight));
		}
	}

}
