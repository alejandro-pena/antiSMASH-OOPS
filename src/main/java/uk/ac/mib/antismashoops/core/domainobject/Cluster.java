package uk.ac.mib.antismashoops.core.domainobject;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Cluster {
    private final List<Gene> queryClusterGenes;
    private final List<BlastHit> blastHits;
    private String name;
	private String source;
	private String type;
	private int proteinsBlasted;
	private int blastScore;

	/**
	 * 
	 * Class Constructor. Initialises the required lists
	 * 
	 */

	public Cluster() {
		queryClusterGenes = new ArrayList<>();
		blastHits = new ArrayList<>();
	}

	/**
	 * 
	 * Class Constructor. Calls the no arguments constructor and sets the file,
	 * origin, number and cluster id attributes.
	 * 
	 * @param name the name of the cluster
	 * @param source the known cluster name
	 * 
	 */

	public Cluster(String name, String source) {
		this();
		this.name = name;
		this.source = source;
	}

	/**
	 * 
	 * Calculates the Blast Hit Score for the cluster hits against a known
	 * cluster.
	 * 
	 * @return The Blast Hit score average of all the hits of the individual
	 *         known cluster.
	 * 
	 */

	public double getBlastHitScore() {
		double score = 0.0;

		for (BlastHit bhe : blastHits) {
			score += (bhe.getCoveragePercentage() * bhe.getIdentityPercentage()) / 100;
		}

		return score / blastHits.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getProteinsBlasted() {
		return proteinsBlasted;
	}

	public void setProteinsBlasted(int proteinsBlasted) {
		this.proteinsBlasted = proteinsBlasted;
	}

	public int getBlastScore() {
		return blastScore;
	}

	public void setBlastScore(int blastScore) {
		this.blastScore = blastScore;
	}

	public List<Gene> getQueryClusterGenes() {
		return queryClusterGenes;
	}

	public List<BlastHit> getBlastHits() {
		return blastHits;
	}
}
