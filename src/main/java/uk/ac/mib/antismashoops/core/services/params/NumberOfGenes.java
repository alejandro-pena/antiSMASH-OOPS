package uk.ac.mib.antismashoops.core.services.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.Gene;

@Service
public class NumberOfGenes
{
    private static final Logger LOG = LoggerFactory.getLogger(NumberOfGenes.class);

    private static final String REGEX = "(.+)(cluster)(.*)(\\.gbk)";
    private static final String ZIP_REGEX = "(.+)(\\.zip)";
    private static final String FOLDER_NAME_KCB = "knownclusterblast";
    private static final String FOLDER_NAME_CB = "clusterblast";
    private static final String FILE_REGEXP = "(cluster)(.*)(\\.txt)";
    private static final String GENES_REGEXP = "Table of genes, locations, strands and annotations of query cluster:";
    private static final String HITS_REGEXP = "Significant hits:";
    private static final String DETAILS_REGEXP = "Details:";
    private static final String HITS_TABLE_REGEXP = "Table of Blast hits (query gene, subject gene, %identity, blast score, %coverage, e-value):";
    private static final String GENE_REGEXP = "(.+)CDS(\\s+)(\\d+\\.\\.\\d+|complement\\(\\d+\\.\\.\\d+\\))";
    private static final String GENEID_REGEXP = "(.+)\\/db_xref=\"GeneID:\\d+\"";
    private static final String GENESYN_REGEXP = "(.+)\\/gene_synonym=\"(.+)\"";
    private static final String SEQUENCE_REGEXP = "a|g|c|t|n";
    private static final String TYPE_REGEXP = "(.+)\\/product=\"(.+)\"(.*)";
    private static final String CLUSTER_REGEXP = "(.+)cluster(.+)(\\d+\\.\\.\\d+)(.*)";


    public void setClusterGeneData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        for (BiosyntheticGeneCluster bgc : biosyntheticGeneClusterList)
        {
            bgc.setGenes(this.getGenesData(bgc.getFile()));
        }
    }


    /**
     * Retrieves the Genes Data from a GBK cluster file. The gene data is a list
     * of Gene Objects.
     *
     * @param file the File object associated the BGC
     * @return An ArrayList of Gene objects
     */

    public List<Gene> getGenesData(File file)
    {
        List<Gene> geneList = new ArrayList<>();
        String geneId = "";
        String geneSynonym = "";
        int startBase;
        int stopBase;
        boolean complement = false;

        Scanner scanner = null;

        try
        {
            scanner = new Scanner(file);
        }
        catch (FileNotFoundException e)
        {
            LOG.error(e.getMessage());
        }

        while (scanner.hasNextLine())
        {
            String token = scanner.nextLine();

            if (token.matches(GENE_REGEXP))
            {
                if (token.contains("complement"))
                {
                    complement = true;
                }
                else
                {
                    complement = false;
                }

                token = token.replaceAll("[A-Za-z]|\\(|\\)|<|>|_", "");
                String[] tokens = token.split("\\.\\.");
                startBase = Integer.parseInt(tokens[0].trim());
                stopBase = Integer.parseInt(tokens[1].trim());

                token = scanner.nextLine();

                if (token.matches(GENEID_REGEXP))
                {
                    geneId = token.replaceAll("[^0-9]", "");
                    scanner.nextLine();
                    token = scanner.nextLine();

                    if (token.matches(GENESYN_REGEXP))
                    {
                        geneSynonym = token.substring(token.indexOf('"') + 1, token.length() - 1);
                    }
                }

                geneList.add(new Gene(geneId, geneSynonym, startBase, stopBase, complement, ""));

            }

        }
        scanner.close();
        return geneList;
    }
}
