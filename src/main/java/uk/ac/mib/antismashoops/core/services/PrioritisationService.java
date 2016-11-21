package uk.ac.mib.antismashoops.core.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainvalue.ClusterSort;

@Component
public class PrioritisationService {

	@Autowired
	ApplicationBgcData appData;

	/**
	 * 
	 * Prioritises the cluster list in a new ArrayList and adds a ponderated
	 * score according to the parameter specified to the global final score.
	 * 
	 * @param comparator Comparator object to understand what type of
	 *            prioritisation is going to be done
	 * @param parameterWeight The parameter ponderation specified by the user
	 * 
	 */

	public void prioritiseParameterAndAddScore(ClusterSort comparator, int parameterWeight) {

		List<BiosyntheticGeneCluster> sortedData = new ArrayList<>(appData.getWorkingDataSet());
		Collections.sort(sortedData, comparator);

		for (BiosyntheticGeneCluster c : appData.getWorkingDataSet()) {
			double score = c.getScore();
			c.setScore(score += ((sortedData.indexOf(c) + 1) * 1.0 * parameterWeight));
		}
	}
}
