package uk.ac.mib.antismashoops.core.service.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.Gene;

@Slf4j
@Service
public class NumberOfGenesService
{
    private static final String GENE_REGEXP = "(.+)CDS(\\s+)(\\d+\\.\\.\\d+|complement\\(\\d+\\.\\.\\d+\\))";
    private static final String GENEID_REGEXP = "(.+)\\/db_xref=\"GeneID:\\d+\"";
    private static final String GENESYN_REGEXP = "(.+)\\/gene_synonym=\"(.+)\"";


    public void setClusterGeneData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setGenes(this.getGenesData(bgc.getFile())));
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setNumberOfGenes(bgc.getGenes().size()));
    }


    /**
     * Retrieves the Genes Data from a GBK cluster file. The gene data is a list
     * of Gene Objects.
     *
     * @param file the File object associated the BGC
     * @return An ArrayList of Gene objects
     */

    private List<Gene> getGenesData(File file)
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
            log.error(e.getMessage());
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
