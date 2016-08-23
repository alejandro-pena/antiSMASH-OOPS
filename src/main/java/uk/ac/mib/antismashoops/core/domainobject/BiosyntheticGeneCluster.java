package uk.ac.mib.antismashoops.core.domainobject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable.Detail;

public class BiosyntheticGeneCluster implements Cloneable {

	// CLUSTER GENERIC ATTRIBUTES

	private final String clusterId;
	private final File file;
	private final String name;
	private final String parent;
	private final String origin;
	private final String number;
	private String clusterType;

	// CLUSTER SPECIFIC ATTRIBUTES

	private List<Gene> genes;

	private String clusterSequence;
	private CodonUsageTable codonUsageTable;
	private double kcScore = 0.0;
	private int selfHomologyScore = -1;
	private Map<Integer, Integer> selfHomologyScores = new HashMap<>();
	private int pdScore = -1;
	private double score = 0.0;

	// CLUSTER SORTING ATTRIBUTES

	private int numberOfGenes;
	private int cdsLength;
	private double gcContent;
	private double gcContentS = -1.0;
	private double codonBiasS = -1.0;

	private static final Logger logger = LoggerFactory.getLogger(BiosyntheticGeneCluster.class);

	public BiosyntheticGeneCluster(File file) {
		this.file = file;
		this.name = removeExtension(file.getName());
		this.parent = file.getParentFile().getName();
		String[] tokens = this.name.split(".c");
		this.origin = tokens[0];
		Integer clusterNo = Integer.parseInt(tokens[1].replaceAll("\\D+", ""));
		this.number = clusterNo.toString();
		this.clusterId = this.origin + "-" + this.number;
	}

	public String getClusterId() {
		return clusterId;
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return parent;
	}

	public String getOrigin() {
		return origin;
	}

	public String getNumber() {
		return number;
	}

	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	public int getCdsLength() {
		return cdsLength;
	}

	public void setCdsLength() {
		int count = 0;
		if (this.genes != null) {
			for (Gene g : this.genes) {
				count += g.getStopBase() - g.getStartBase();
				count++;
			}
		}
		this.cdsLength = count;
	}

	public List<Gene> getGenes() {
		return genes;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}

	public int getNumberOfGenes() {
		return numberOfGenes;
	}

	public void setNumberOfGenes(int numberOfGenes) {
		this.numberOfGenes = numberOfGenes;
	}

	public double getGcContent() {
		return gcContent;
	}

	public void setGcContent() {
		int count = 0;
		for (Gene g : this.genes) {
			String geneSeq = this.clusterSequence.substring(g.getStartBase() - 1, g.getStopBase());
			for (int i = 0; i < geneSeq.length(); i++) {
				if (geneSeq.charAt(i) == 'C' || geneSeq.charAt(i) == 'G')
					count++;
			}
		}
		this.gcContent = count * 100.0 / this.cdsLength;
	}

	public CodonUsageTable getCodonUsageTable() {
		return codonUsageTable;
	}

	public void setCodonUsageTable() {
		this.codonUsageTable = this.computeCodonUsageTable();
	}

	public String getClusterSequence() {
		return clusterSequence;
	}

	public void setClusterSequence(String clusterSequence) {
		this.clusterSequence = clusterSequence;
	}

	public Set<String> getClusterTypes() {
		Set<String> clusterTypes = new HashSet<>();

		String[] types = this.clusterType.split("-");
		for (String s : types) {
			clusterTypes.add(s.trim());
		}
		return clusterTypes;
	}

	@Override
	public String toString() {
		return "Cluster [Cluster=" + origin + number + ", kcScore=" + kcScore + "]";
	}

	private static String removeExtension(String name) {
		return name.substring(0, name.lastIndexOf('.'));
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getGcContentS() {
		return gcContentS;
	}

	public void setGcContentS(double gcContentRef) {
		this.gcContentS = Math.abs(this.gcContent - gcContentRef);
	}

	public double getCodonBiasS() {
		return codonBiasS;
	}

	public void setCodonBiasS(CodonUsageTable cutRef) {
		double codonBiasScore = 0.0;

		// ITERATING ACROSS ALL THE ROWS OF THE DETAIL OBJECT (AMINO ACIDS) AND
		// ADDING UP THE ABSOLUTE DIFFERENCE BETWEEN THE SPECIES AND THE CLUSTER

		for (Entry<String, Detail> cut : this.codonUsageTable.getUsage().entrySet()) {
			Detail dCluster = cut.getValue();
			Detail dRef = cutRef.getUsage().get(cut.getKey());
			codonBiasScore += Math.abs(dCluster.getAminoacidUsage() - dRef.getAminoacidUsage());
		}
		this.codonBiasS = codonBiasScore;
	}

	public double getKcScore() {
		return kcScore;
	}

	public void setKcScore(double kcScore) {
		this.kcScore = kcScore;
	}

	public int getSelfHomologyScore() {
		return selfHomologyScore;
	}

	public void setSelfHomologyScore(int selfHomologyScore) {
		this.selfHomologyScore = selfHomologyScore;
	}

	public Map<Integer, Integer> getSelfHomologyScores() {
		return selfHomologyScores;
	}

	public void setSelfHomologyScores(Map<Integer, Integer> selfHomologyScores) {
		this.selfHomologyScores = selfHomologyScores;
	}

	public int getPdScore() {
		return pdScore;
	}

	public void setPdScore(int pdScore) {
		this.pdScore = pdScore;
	}

	public CodonUsageTable computeCodonUsageTable() {

		String sequence;
		int codonTotal;

		CodonUsageTable cut = new CodonUsageTable();
		int cdsLength = this.getCdsLength();

		for (Gene g : genes) {
			sequence = clusterSequence.substring(g.getStartBase() - 1, g.getStopBase());
			if (g.isComplement()) {
				sequence = getComplementSequence(sequence);
			}

			codonTotal = sequence.length() / 3;

			// POPULATES THE NUMBER OF OCCURRENCES PER CODON

			for (int i = 1; i <= codonTotal; i++) {
				CodonUsageTable.Detail d = cut.getUsage().get(sequence.substring(i * 3 - 3, i * 3));
				d.setCodonNumber(d.getCodonNumber() + 1);
			}
		}

		// POPULATES THE USAGE FREQUENCY PER CODON

		cut.setCodonFrequencies(cdsLength);

		// POPULATE THE USAGE PERCENTAGE PER AMINO ACID

		cut.setUsagePerAminoacids();

		return cut;
	}

	public String getComplementSequence(String sequence) {
		StringBuilder sb = new StringBuilder();
		String reversedSequence = new StringBuilder(sequence).reverse().toString();

		for (int i = 0; i < reversedSequence.length(); i++) {
			switch (reversedSequence.charAt(i)) {
			case 'A':
				sb.append("T");
				break;
			case 'C':
				sb.append("G");
				break;
			case 'G':
				sb.append("C");
				break;
			case 'T':
				sb.append("A");
				break;
			}
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BiosyntheticGeneCluster other = (BiosyntheticGeneCluster) obj;
		if (clusterId == null) {
			if (other.clusterId != null)
				return false;
		} else if (!clusterId.equals(other.clusterId))
			return false;
		return true;
	}

	@Override
	protected BiosyntheticGeneCluster clone() {
		BiosyntheticGeneCluster clone = null;
		try {
			clone = (BiosyntheticGeneCluster) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
