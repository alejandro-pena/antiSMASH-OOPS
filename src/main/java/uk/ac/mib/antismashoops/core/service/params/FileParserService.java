package uk.ac.mib.antismashoops.core.service.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

@Service
public class FileParserService
{
    public void setClusterSpeciesData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setSpecies(this.getClusterSpecies(bgc.getFile())));
    }


    /**
     * Retrieves the Cluster Species from a GBK cluster file.
     *
     * @param file the File object associated the BGC
     * @return A string containing species associated with the BGC
     */

    private String getClusterSpecies(File file)
    {
        Scanner scanner = null;
        String species = "";
        try
        {
            scanner = new Scanner(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        if (scanner.hasNextLine())
        {
            scanner.nextLine();
        }
        if (scanner.hasNextLine())
        {
            String nextLine = scanner.nextLine();
            String[] tokens = nextLine.split("DEFINITION");
            if (tokens.length > 0)
            {
                species = tokens[1].trim();
            }
        }
        return species;
    }
}
