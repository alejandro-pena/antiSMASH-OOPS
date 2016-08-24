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

	private static final Logger logger = LoggerFactory.getLogger(BiosyntheticGeneCluster.class);

	// BGC GENERIC ATTRIBUTES

	private final String clusterId;
	private final File file;
	private final String name;
	private final String parent;
	private final String origin;
	private final String number;
	private String clusterType;

	// CLUSTER SCORING ATTRIBUTES

	private int numberOfGenes;
	private int cdsLength;
	private double gcContent;
	private double gcContentS = -1.0;
	private double codonBiasS = -1.0;
	private double kcScore = 0.0;
	private int selfHomologyScore = -1;
	private Map<Integer, Integer> selfHomologyScores = new HashMap<>();
	private int diversityScore = -1;

	// FINAL CLUSTER SCORE
	private double score = 0.0;

	// BGC COMPLEX ATTRIBUTES

	private List<Gene> genes;
	private String clusterSequence;
	private CodonUsageTable codonUsageTable;
	private KnownCluster knownClustersData;
	private ClusterBlast clusterBlastsData;

	/**
	 * Class constructor. Assigns 6 attributes for the biosynthetic gene
	 * cluster: file, name, parent file, origin, number and cluster Id
	 * 
	 * @param file The File object the BGC is associated to.
	 */

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

	/**
	 * 
	 * GETTERS AND SETTERS FOR THE BGC BASIC ATTRIBUTES
	 * 
	 */

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

	/**
	 * 
	 * GETTERS AND SETTERS FOR THE PARAMETER SCORE HOLDERS
	 * 
	 */

	// NUMBER OF GENES

	public int getNumberOfGenes() {
		return numberOfGenes;
	}

	public void setNumberOfGenes(int numberOfGenes) {
		this.numberOfGenes = numberOfGenes;
	}

	// CDS LENGTH

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

	// GC CONTENT

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

	// GC CONTENT CONSIDERING A REFERENCE SPECIES

	public double getGcContentS() {
		return gcContentS;
	}

	public void setGcContentS(double gcContentRef) {
		this.gcContentS = Math.abs(this.gcContent - gcContentRef);
	}

	// CODON BIAS

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

	// KNOWN CLUSTER SIMILARITY

	public double getKcScore() {
		return kcScore;
	}

	public void setKcScore(double kcScore) {
		this.kcScore = kcScore;
	}

	// SELF-HOMOLOGY

	public int getSelfHomologyScore() {
		return selfHomologyScore;
	}

	public void setSelfHomologyScore(int selfHomologyScore) {
		this.selfHomologyScore = selfHomologyScore;
	}

	// SELF-HOMOLOGY CACHING MAP

	public Map<Integer, Integer> getSelfHomologyScores() {
		return selfHomologyScores;
	}

	public void setSelfHomologyScores(Map<Integer, Integer> selfHomologyScores) {
		this.selfHomologyScores = selfHomologyScores;
	}

	// PHYLOGENETIC DIVERSITY

	public int getDiversityScore() {
		return diversityScore;
	}

	public void setDiversityScore(int diversityScore) {
		this.diversityScore = diversityScore;
	}

	// BGC FINAL SCORE

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * 
	 * GETTERS AND SETTERS FOR THE BASIC ATTRIBUTES
	 * 
	 */

	public List<Gene> getGenes() {
		return genes;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}

	public String getClusterSequence() {
		return clusterSequence;
	}

	public void setClusterSequence(String clusterSequence) {
		this.clusterSequence = clusterSequence;
	}

	public CodonUsageTable getCodonUsageTable() {
		return codonUsageTable;
	}

	public void setCodonUsageTable() {
		this.codonUsageTable = this.computeCodonUsageTable();
	}

	public KnownCluster getKnownClustersData() {
		return knownClustersData;
	}

	public void setKnownClustersData(KnownCluster knownClustersData) {
		this.knownClustersData = knownClustersData;
	}

	public ClusterBlast getClusterBlastsData() {
		return clusterBlastsData;
	}

	public void setClusterBlastsData(ClusterBlast clusterBlastsData) {
		this.clusterBlastsData = clusterBlastsData;
	}

	/**
	 * Provides a set object including the type or types the BGC belongs to
	 * 
	 * @return A Set object with the cluster type or types as strings
	 * 
	 */

	public Set<String> getClusterTypes() {
		Set<String> clusterTypes = new HashSet<>();

		String[] types = this.clusterType.split("-");
		for (String s : types) {
			clusterTypes.add(s.trim());
		}
		return clusterTypes;
	}

	/**
	 * Removes the extension of a string
	 * 
	 * @param name The String to remove the extension from.
	 * 
	 * @return A string with the trimmed extension.
	 * 
	 */

	private static String removeExtension(String name) {
		return name.substring(0, name.lastIndexOf('.'));
	}

	/**
	 * 
	 * Computes a codon usage table using the cluster sequence and genes
	 * information.
	 * 
	 * @return A CodonUsageTable with the BGC information
	 * 
	 */

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

	/**
	 * 
	 * Converts any nucleotide sequence into its complementary sequence
	 * (Inverting the string and getting the complement)
	 * 
	 * @param sequence A nucleotide sequence in a String format
	 * 
	 * @return The complementary sequence
	 * 
	 */

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

	/**
	 * Two clusters are considered equal if the share the exact same Cluster Id
	 * 
	 * @param obj The Cluster Object to be compared
	 * 
	 * @return If the cluster ids are equal returns true, else false.
	 * 
	 */

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

	/**
	 * Creates a copy of the BGC
	 * 
	 * @return A hard copy Biosynthetic Gene Cluster
	 * 
	 */

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
