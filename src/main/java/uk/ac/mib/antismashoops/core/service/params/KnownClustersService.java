package uk.ac.mib.antismashoops.core.service.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.BlastHit;
import uk.ac.mib.antismashoops.core.domainobject.Cluster;
import uk.ac.mib.antismashoops.core.domainobject.Gene;
import uk.ac.mib.antismashoops.core.domainobject.KnownCluster;
import uk.ac.mib.antismashoops.web.utils.Workspace;

@Slf4j
@Service
public class KnownClustersService
{
    @Value("${app.files.uploadpath}")
    private String uploadPath;

    private static final String HITS_TABLE_REGEXP = "Table of Blast hits (query gene, subject gene, %identity, blast score, %coverage, e-value):";
    private static final String GENES_REGEXP = "Table of genes, locations, strands and annotations of query cluster:";
    private static final String FOLDER_NAME_KCB = "knownclusterblast";
    private static final String FILE_REGEXP = "(.*_c)(.*)(\\.txt)";
    private static final String GBK_FILE_REGEX = "(.+)(region)(.*)(\\.gbk)";
    private static final String HITS_REGEXP = "Significant hits:";
    private static final String DETAILS_REGEXP = "Details:";


    /**
     * Sets the Known Cluster Similarity score for each BGC in the application.
     * It the Best Match Score calling the method from each KnownCluster object
     * associated to the BGCs
     *
     * @param preferredSimilarity The preferred similarity percentage specified
     *                            by the user
     */

    public void setKnownClusterSimilarityScore(ApplicationBgcData appData, double preferredSimilarity, int plusMinusValue)
    {
        for (BiosyntheticGeneCluster bgc : appData.getWorkingDataSet())
        {
            KnownCluster kc = bgc.getKnownClustersData();
            if (kc != null)
            {
                bgc.setKcScore(kc.getBestMatchScore(preferredSimilarity, plusMinusValue));
            }
        }
    }


    public void setPreprocessedKnownClusterSimilarityScore(ApplicationBgcData appData, double preferredSimilarity, int plusMinusValue)
    {
        for (BiosyntheticGeneCluster bgc : appData.getPreprocessedWorkingDataSet())
        {
            KnownCluster kc = bgc.getKnownClustersData();
            if (kc != null)
            {
                bgc.setKcScore(kc.getBestMatchScore(preferredSimilarity, plusMinusValue));
            }
        }
    }


    /**
     * Creates the Known Cluster objects scanning all the zip files loaded into
     * the application and triggers its population at the end of the function.
     */

    public void setKnownClusterData(ApplicationBgcData appData, Workspace workspace)
    {
        List<KnownCluster> knownClusterList = new ArrayList<>();

        File root = workspace.getRoot();
        File[] list;
        if (!root.exists())
        {
            return;
        }
        else
        {
            list = root.listFiles();
            if (list.length == 0)
            {
                return;
            }
        }

        for (File parent : list)
        {
            if (parent.isDirectory())
            {
                String clusterFamily = this.getClusterFamilyName(parent);
                File folder = new File(uploadPath + workspace.getName() + "/" + parent.getName(), FOLDER_NAME_KCB);
                if (!folder.exists())
                {
                    continue;
                }
                for (File file : folder.listFiles())
                {
                    if (file.getName().matches(FILE_REGEXP))
                    {
                        //						Scanner scanner = null;
                        //						try {
                        //							scanner = new Scanner(file);
                        //						} catch (FileNotFoundException e) {
                        //							e.printStackTrace();
                        //						}
                        //
                        //						String[] tokens = scanner.nextLine().split(" ");
                        //						String origin = tokens[tokens.length - 1];
                        String number = file.getName().split("c")[1].replaceAll("[^0-9]", "");
                        knownClusterList.add(new KnownCluster(file, clusterFamily, number));
                    }
                }
            }
        }

        Iterator<KnownCluster> it = knownClusterList.iterator();
        while (it.hasNext())
        {

            KnownCluster cbe = it.next();

            boolean found = false;
            for (BiosyntheticGeneCluster c : appData.getWorkingDataSet())
            {
                if (cbe.getClusterId().equals(c.getClusterId()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                it.remove();
            }
        }
        populateKnownClusterData(knownClusterList, appData);
    }


    /**
     * Retrieves the Known Cluster data from the knowncluster folder of each ZIP
     * file entry loaded to the application and sets the associated data to each
     * respective BGC object
     *
     * @param knownClusterList A List of KnownCluster objects that hold the Known Cluster
     *                         Data of all the BGCs loaded in the application.
     */

    private void populateKnownClusterData(List<KnownCluster> knownClusterList, ApplicationBgcData appData)
    {
        for (KnownCluster kce : knownClusterList)
        {
            Scanner scanner = null;
            try
            {
                scanner = new Scanner(kce.getFile());

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (nextLine.trim().matches(GENES_REGEXP))
                {
                    break;
                }
            }

            // SET THE GENES OF THE QUERY CLUSTER (BGC)

            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (!(nextLine.trim()).matches(HITS_REGEXP) && !nextLine.trim().matches(""))
                {
                    String[] tokens = nextLine.split("\\t");
                    kce.getBgcGenes()
                        .add(new Gene(tokens[0], "", Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                            tokens[3].equals("+"), tokens.length > 4 ? tokens[4] : ""));
                }
                else
                {
                    break;
                }
            }

            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (nextLine.trim().matches(DETAILS_REGEXP) && scanner.hasNextLine())
                {
                    break;
                }
            }

            // SET THE CLUSTER HITS FOR THE BGC

            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (nextLine.trim().matches(">>"))
                {
                    Cluster c = new Cluster();

                    // SET CLUSTER NAME
                    nextLine = scanner.nextLine();
                    String[] tokens = nextLine.split(" ");
                    c.setName(tokens[tokens.length - 1]);

                    // SET CLUSTER SOURCE
                    nextLine = scanner.nextLine();
                    tokens = nextLine.split(" ");
                    c.setSource(tokens[tokens.length - 1]);

                    // SET CLUSTER TYPE
                    nextLine = scanner.nextLine();
                    tokens = nextLine.split(" ");
                    c.setType(tokens[tokens.length - 1]);

                    // SET CLUSTER BLAST PROTEINS
                    nextLine = scanner.nextLine();
                    tokens = nextLine.split(" ");
                    c.setProteinsBlasted(Integer.parseInt(tokens[tokens.length - 1]));

                    // SET BLAST SCORE
                    nextLine = scanner.nextLine();
                    tokens = nextLine.split(" ");
                    c.setProteinsBlasted(Integer.parseInt(tokens[tokens.length - 1]));

                    scanner.nextLine();
                    scanner.nextLine();

                    // SET THE GENES OF THE CLUSTER

                    while (scanner.hasNextLine())
                    {
                        nextLine = scanner.nextLine();
                        if (!(nextLine.trim()).matches(HITS_TABLE_REGEXP) && !nextLine.trim().matches(""))
                        {
                            tokens = nextLine.split("\\t");
                            c.getQueryClusterGenes()
                                .add(new Gene(tokens[1], "", Integer.parseInt(tokens[2]),
                                    Integer.parseInt(tokens[3]), tokens[4].equals("+"),
                                    tokens.length > 5 ? tokens[5] : ""));
                        }
                        else
                        {
                            break;
                        }
                    }

                    scanner.nextLine();

                    // SET THE BLAST HITS FOR THE CLUSTER

                    while (scanner.hasNextLine())
                    {
                        nextLine = scanner.nextLine();
                        if (!(nextLine.trim()).matches(">>") && !nextLine.trim().matches(""))
                        {
                            tokens = nextLine.split("\\t|\\s");
                            c.getBlastHits().add(new BlastHit(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
                                Integer.parseInt(tokens[3]), Double.parseDouble(tokens[4]), tokens[5]));
                        }
                        else
                        {
                            break;
                        }
                    }

                    kce.getClusterHits().add(c);
                }

            }
            appData.getCluster(kce.getClusterId()).setKnownClustersData(kce);
        }
    }


    private String getClusterFamilyName(File root)
    {
        String family = "";
        for (File f : root.listFiles())
        {
            if (f.getName().matches(GBK_FILE_REGEX))
            {
                return f.getName().replaceAll("\\.(region)(.*)(\\.gbk)", "");
            }
        }
        return family;
    }

}
