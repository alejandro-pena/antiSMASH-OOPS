package uk.ac.mib.antismashoops.core.domainobject;

public class BlastHit {
	private String queryGene;
	private String subjectGene;
	private double identityPercentage;
	private int blastScore;
	private double coveragePercentage;
	private String eValue;

	public BlastHit() {
	}

	/**
	 * 
	 * Class Constructor. Sets the class attributes.
	 * 
	 * @param queryGene The name of the query gene
	 * @param subjectGene The name of the subject found gene
	 * @param identityPercentage The percentage of how identical are those
	 * @param blastScore The Blast score from antiSMASH
	 * @param coveragePercentage Coverage percentage of the queried gene
	 * @param eValue
	 * 
	 */

	public BlastHit(String queryGene, String subjectGene, double identityPercentage, int blastScore,
			double coveragePercentage, String eValue) {
		this.queryGene = queryGene;
		this.subjectGene = subjectGene;
		this.identityPercentage = identityPercentage;
		this.blastScore = blastScore;
		this.coveragePercentage = coveragePercentage;
		this.eValue = eValue;
	}

	public String getQueryGene() {
		return queryGene;
	}

	public void setQueryGene(String queryGene) {
		this.queryGene = queryGene;
	}

	public String getSubjectGene() {
		return subjectGene;
	}

	public void setSubjectGene(String subjectGene) {
		this.subjectGene = subjectGene;
	}

	public double getIdentityPercentage() {
		return identityPercentage;
	}

	public void setIdentityPercentage(double identityPercentage) {
		this.identityPercentage = identityPercentage;
	}

	public int getBlastScore() {
		return blastScore;
	}

	public void setBlastScore(int blastScore) {
		this.blastScore = blastScore;
	}

	public double getCoveragePercentage() {
		return coveragePercentage;
	}

	public void setCoveragePercentage(double coveragePercentage) {
		this.coveragePercentage = coveragePercentage;
	}

	public String geteValue() {
		return eValue;
	}

	public void seteValue(String eValue) {
		this.eValue = eValue;
	}

	@Override
	public String toString() {
		return "BlastHitEntry [queryGene=" + queryGene + ", subjectGene=" + subjectGene + ", identityPercentage="
				+ identityPercentage + ", blastScore=" + blastScore + ", coveragePercentage=" + coveragePercentage
				+ ", eValue=" + eValue + "]";
	}

}
