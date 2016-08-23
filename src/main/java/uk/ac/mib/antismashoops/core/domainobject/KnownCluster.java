package uk.ac.mib.antismashoops.core.domainobject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class KnownCluster {
	private String clusterId;
	private File file;
	private String origin;
	private String number;
	private List<Gene> clusterGenes;
	private List<ClusterFw> clusterHits;

	public KnownCluster() {
		clusterGenes = new ArrayList<>();
		clusterHits = new ArrayList<>();
	}

	public KnownCluster(File file, String origin, String number) {
		this();
		this.file = file;
		this.origin = origin;
		this.number = number;
		this.setClusterId(this.origin + "-" + this.number);
	}

	public double getBestMatchScore(double preferredSimilarity) {
		if (this.getClusterHits().size() == 0)
			return 100.0;

		ClusterFw mostSimilar = getClusterHits().get(0);
		double score = Math
				.abs(preferredSimilarity - ((mostSimilar.getBlastHits().size() * 100 / getClusterGenes().size())
						* mostSimilar.getBlastHitScore() / 100));

		for (ClusterFw c : getClusterHits()) {
			double newScore = Math.abs(preferredSimilarity
					- ((c.getBlastHits().size() * 100 / getClusterGenes().size()) * c.getBlastHitScore() / 100));
			if (newScore < score) {
				mostSimilar = c;
				score = newScore;
			}
		}
		return score;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getRecordName() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<Gene> getClusterGenes() {
		return clusterGenes;
	}

	public void setClusterGenes(List<Gene> clusterGenes) {
		this.clusterGenes = clusterGenes;
	}

	public List<ClusterFw> getClusterHits() {
		return clusterHits;
	}

	public void setClusterHits(List<ClusterFw> clusterHits) {
		this.clusterHits = clusterHits;
	}

	@Override
	public String toString() {
		return "KnownClusterEntry [file=" + file + ", recordName=" + origin + ", clusterNumber=" + number + "]";
	}

}
