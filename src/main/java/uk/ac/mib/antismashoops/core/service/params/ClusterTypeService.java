package uk.ac.mib.antismashoops.core.service.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

@Service
public class ClusterTypeService
{
    private static final String TYPE_REGEXP = "(.+)\\/product=\"(.+)\"(.*)";
    private static final String CLUSTER_REGEXP = "(.+)cluster(.+)(\\d+\\.\\.\\d+)(.*)";


    public void setClusterTypeData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setClusterType(this.getClusterType(bgc.getFile())));
    }


    /**
     * Retrieves the Cluster Type from a GBK cluster file.
     *
     * @param file the File object associated the BGC
     * @return A string containing the type or types splitted by a hyphen
     */

    private String getClusterType(File file)
    {
        Scanner scanner;
        StringBuilder sb = new StringBuilder();

        try
        {
            scanner = new Scanner(file);
            String nextToken = "";

            while (scanner.hasNextLine())
            {
                nextToken = scanner.nextLine();
                if (nextToken.matches(CLUSTER_REGEXP))
                {
                    break;
                }
            }

            while (scanner.hasNextLine())
            {
                nextToken = scanner.nextLine();
                if (nextToken.matches(TYPE_REGEXP))
                {
                    sb.append(nextToken);
                    break;
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

        String type = sb.toString();
        type = sb.substring(sb.indexOf("\"") + 1, sb.lastIndexOf("\"")).trim();

        return type;
    }
}
