package uk.ac.mib.antismashoops.core.domainobject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnownCluster
{
    private String clusterId;
    private File file;
    private String origin;
    private String number;
    private List<Gene> bgcGenes;
    private List<Cluster> clusterHits;


    /**
     * Class Constructor. Initialises the required Lists
     */

    private KnownCluster()
    {
        bgcGenes = new ArrayList<>();
        clusterHits = new ArrayList<>();
    }


    /**
     * Class Constructor. Calls the no arguments constructor and sets the file,
     * origin, number and cluster id attributes.
     *
     * @param file   the File object associated to this KnownCluster record
     * @param origin the cluster family (zip file name)
     * @param number the known cluster number associated to the BGC
     */

    public KnownCluster(File file, String origin, String number)
    {
        this();
        this.file = file;
        this.origin = origin;
        this.number = number;
        this.setClusterId(this.origin + "-" + this.number);
    }


    /**
     * Iterates over the KnownCluster data finding the most similar cluster to
     * the specified similarity percentage
     *
     * @param preferredSimilarity The similarity preferred percentage from the user
     * @return The smallest difference between the Known Clusters and the
     * requested similarity percentage
     */

    public double getBestMatchScore(double preferredSimilarity, int plusMinusValue)
    {

        if (this.getClusterHits().size() == 0)
        {
            return 100.0;
        }

        Cluster mostSimilar = getClusterHits().get(0);
        double score = Math.abs(preferredSimilarity - ((mostSimilar.getBlastHits().size() * 100 / getBgcGenes().size())
            * mostSimilar.getBlastHitScore() / 100));

        for (Cluster c : getClusterHits())
        {
            double newScore = Math.abs(preferredSimilarity
                - ((c.getBlastHits().size() * 100 / getBgcGenes().size()) * c.getBlastHitScore() / 100));
            if (newScore < score)
            {
                mostSimilar = c;
                score = newScore;
            }
        }
        return score;
    }
}
