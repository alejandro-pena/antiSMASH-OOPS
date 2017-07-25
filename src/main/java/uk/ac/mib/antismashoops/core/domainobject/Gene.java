package uk.ac.mib.antismashoops.core.domainobject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class Gene
{

    private String geneId;
    private String synonym;
    private int startBase;
    private int stopBase;
    private boolean complement;
    private String annotation;


    public Gene()
    {
    }


    /**
     * Class constructor.
     *
     * @param geneId     String containing the gene id
     * @param synonym    String containing the gene synonym
     * @param startBase  The exact starting base number in the coding sequence
     * @param stopBase   The exact stopping base number in the coding sequence
     * @param complement If the sequence must be read in a complementary way
     * @param annotation Further annotations
     */

    public Gene(String geneId, String synonym, int startBase, int stopBase, boolean complement, String annotation)
    {
        this.geneId = geneId;
        this.synonym = synonym;
        this.startBase = startBase;
        this.stopBase = stopBase;
        this.complement = complement;
        this.annotation = annotation;
    }
}
