package uk.ac.mib.antismashoops.core.service.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

@Slf4j
@Service
public class CdsLengthService
{
    private static final String SEQUENCE_REGEXP = "[agctn]";


    public void setClusterSequenceData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setClusterSequence(this.getClusterSequence(bgc.getFile())));
        biosyntheticGeneClusterList.forEach(BiosyntheticGeneCluster::setCdsLength);
    }


    /**
     * Retrieves the entire cluster sequence from a GBK file.
     *
     * @param file the File object associated the BGC
     * @return A string with the nucleotide sequence in uppercase letters.
     */

    private String getClusterSequence(File file)
    {
        Scanner scanner;
        StringBuilder sb = new StringBuilder();

        try
        {
            scanner = new Scanner(file);

            while (scanner.hasNext())
            {
                String nextToken = scanner.next();
                if (nextToken.equalsIgnoreCase("ORIGIN"))
                {
                    break;
                }
            }

            scanner.useDelimiter("");

            while (scanner.hasNext())
            {
                String nextToken = scanner.next();
                if (nextToken.matches(SEQUENCE_REGEXP))
                {
                    sb.append(nextToken);
                }
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return sb.toString().toUpperCase();
    }
}
