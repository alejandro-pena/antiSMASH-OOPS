package uk.ac.mib.antismashoops.core.domainobject;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlastHit
{
    private String queryGene;
    private String subjectGene;
    private double identityPercentage;
    private int blastScore;
    private double coveragePercentage;
    private String eValue;


    /**
     * Class Constructor. Sets the class attributes.
     *
     * @param queryGene          The name of the query gene
     * @param subjectGene        The name of the subject found gene
     * @param identityPercentage The percentage of how identical are those
     * @param blastScore         The Blast score from antiSMASH
     * @param coveragePercentage Coverage percentage of the queried gene
     * @param eValue             Extra value
     */

    public BlastHit(
        String queryGene, String subjectGene, double identityPercentage, int blastScore,
        double coveragePercentage, String eValue)
    {
        this.queryGene = queryGene;
        this.subjectGene = subjectGene;
        this.identityPercentage = identityPercentage;
        this.blastScore = blastScore;
        this.coveragePercentage = coveragePercentage;
        this.eValue = eValue;
    }
}
