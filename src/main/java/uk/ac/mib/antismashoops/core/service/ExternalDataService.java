package uk.ac.mib.antismashoops.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.BlastHit;
import uk.ac.mib.antismashoops.core.domainobject.Cluster;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlast;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastLineage;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainobject.Gene;
import uk.ac.mib.antismashoops.core.domainobject.KnownCluster;
import uk.ac.mib.antismashoops.core.service.params.CdsLengthService;
import uk.ac.mib.antismashoops.core.service.params.ClusterTypeService;
import uk.ac.mib.antismashoops.core.service.params.CodonBiasService;
import uk.ac.mib.antismashoops.core.service.params.FileParserService;
import uk.ac.mib.antismashoops.core.service.params.GcContentService;
import uk.ac.mib.antismashoops.core.service.params.NumberOfGenesService;
import uk.ac.mib.antismashoops.web.utils.Workspace;
import uk.ac.mib.antismashoops.web.utils.ZipFileHandler;

@Slf4j
@Service
public class ExternalDataService
{
    private static final Logger LOG = LoggerFactory.getLogger(ExternalDataService.class);
    private static final String GBK_FILE_REGEX = "(.+)(cluster)(.*)(\\.gbk)";
    private static final String ZIP_FILE_REGEX = "(.+)(\\.zip)";
    private static final String FOLDER_NAME_KCB = "knownclusterblast";
    private static final String FOLDER_NAME_CB = "clusterblast";
    private static final String FILE_REGEXP = "(cluster)(.*)(\\.txt)";
    private static final String GENES_REGEXP = "Table of genes, locations, strands and annotations of query cluster:";
    private static final String HITS_REGEXP = "Significant hits:";
    private static final String DETAILS_REGEXP = "Details:";
    private static final String HITS_TABLE_REGEXP = "Table of Blast hits (query gene, subject gene, %identity, blast score, %coverage, e-value):";
    private static final String ANTI_SMASH_PREPROCESSED_DATA_PREFIX = "APPS_";

    @Value("${app.files.uploadpath}")
    private String uploadPath;

    private final OnlineResourceService onlineResourceService;
    private final NumberOfGenesService numberOfGenesService;
    private final CdsLengthService cdsLengthService;
    private final GcContentService gcContentService;
    private final CodonBiasService codonBiasService;
    private final ClusterTypeService clusterTypeService;
    private final FileParserService fileParserService;


    @Autowired
    public ExternalDataService(
        NumberOfGenesService numberOfGenesService,
        CdsLengthService cdsLengthService,
        GcContentService gcContentService,
        CodonBiasService codonBiasService,
        ClusterTypeService clusterTypeService,
        FileParserService fileParserService,
        OnlineResourceService onlineResourceService)
    {
        this.numberOfGenesService = numberOfGenesService;
        this.onlineResourceService = onlineResourceService;
        this.cdsLengthService = cdsLengthService;
        this.gcContentService = gcContentService;
        this.codonBiasService = codonBiasService;
        this.clusterTypeService = clusterTypeService;
        this.fileParserService = fileParserService;
    }


    /**
     * Decompresses the ZIP files loaded into the application. If no files are
     * loaded no action is taken by the function.
     */

    public void decompressLoadedFiles(Workspace workspace)
    {

        File root = workspace.getRoot();
        if (!root.exists())
        {
            return;
        }

        // If the Workspace name is prefixed by APPS the ZIP files won't be decompressed again.
        // We assume that the contents are already antiSMASH preprocessed data.
        if (root.getName().contains(ANTI_SMASH_PREPROCESSED_DATA_PREFIX))
        {
            log.info("Skipping file decompression in Workspace: {}", workspace.getName());
            return;
        }

        for (File parent : root.listFiles())
        {
            if (parent.isFile() && parent.getName().matches(ZIP_FILE_REGEX))
            {
                ZipFileHandler.decompressFile(parent, uploadPath + workspace.getName());
            }
        }

        File __MACOSX = new File(uploadPath + workspace.getName(), "__MACOSX");
        if (__MACOSX.exists())
        {
            this.delete(__MACOSX);
        }
    }


    /**
     * Constructs a BGC object per GBK file found in the folder or folders
     * created after decompressing the ZIP file(s), passing to the constructor
     * the File object pointing to the GBK source file. If the list contains any
     * BGC object it will be cleared. Calls the initial data population.
     *
     * @param bgcData The empty or populated List of the BGC Objects.
     */

    public void loadBggData(List<BiosyntheticGeneCluster> bgcData, Workspace workspace)
    {

        bgcData.clear();
        File root = workspace.getRoot();
        if (!root.exists())
        {
            return;
        }

        for (File parent : workspace.getRoot().listFiles())
        {
            if (parent.isDirectory())
            {
                File folder = new File(uploadPath + workspace.getName(), parent.getName());
                File[] files = folder.listFiles();
                for (File f : files)
                {
                    if (f.getName().matches(GBK_FILE_REGEX))
                    {
                        bgcData.add(new BiosyntheticGeneCluster(f));
                    }
                }
            }
        }
        populateClusterData(bgcData);
    }


    /**
     * Populates six attributes per BGC: Genes data, cluster sequence, number of
     * genes, CDS length, GC Content and Cluster Type.
     *
     * @param bgcData The populated List of the BGC Objects having the File
     *                attribute set to point their respective GBK file.
     */

    private void populateClusterData(List<BiosyntheticGeneCluster> bgcData)
    {
        numberOfGenesService.setClusterGeneData(bgcData);
        cdsLengthService.setClusterSequenceData(bgcData);
        gcContentService.setGcContentData(bgcData);
        codonBiasService.setCodonBiasData(bgcData);
        clusterTypeService.setClusterTypeData(bgcData);
        fileParserService.setClusterSpeciesData(bgcData);
    }


    /**
     * Populates the GC Content Score and the Codon Usage Score for the BGC Data
     *
     * @param bgcData      The populated List of the BGC Objects.
     * @param gcContentRef The GC Content of the reference species
     * @param cutRef       The Codon Usage Table object of the reference species
     */

    public void populateClusterData(List<BiosyntheticGeneCluster> bgcData, double gcContentRef, CodonUsageTable cutRef)
    {
        gcContentService.setGcContentDataWithReferenceSpecies(bgcData, gcContentRef);
        codonBiasService.setCodonBiasDataWithReferenceSpecies(bgcData, cutRef);
    }


    /**
     * Verifies if the number of GBK files loaded is the same as the number of
     * BGC Objects in the Application Data BGC List.
     *
     * @param bgcDataSize The number of BGCs in the Application Data.
     * @return true if the size is the same and different from zero, if the
     * cluster list is empty or not in sync returns false.
     */

    public boolean isBgcDataInSync(int bgcDataSize, Workspace workspace)
    {
        if (bgcDataSize == 0)
        {
            return false;
        }

        int zipFiles = 0;
        int directories = 0;
        int gbk_files = 0;

        File root = workspace.getRoot();
        File[] list;
        if (!root.exists())
        {
            return false;
        }
        else
        {
            list = root.listFiles();
            if (list.length == 0)
            {
                return false;
            }
        }

        for (File parent : list)
        {
            if (parent.isDirectory())
            {
                directories++;
                File folder = new File(uploadPath + workspace.getName(), parent.getName());
                File[] files = folder.listFiles();
                for (File f : files)
                {
                    if (f.getName().matches(GBK_FILE_REGEX))
                    {
                        gbk_files++;
                    }
                }
            }
            else if (parent.getName().matches(ZIP_FILE_REGEX))
            {
                zipFiles++;
            }
        }
        if (gbk_files == bgcDataSize && directories == zipFiles)
        {
            return true;
        }
        return false;
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
                File folder = new File(
                    uploadPath + workspace.getName() + "/" + parent.getName(),
                    FOLDER_NAME_KCB);
                if (!folder.exists())
                {
                    return;
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
                        String number = file.getName().replaceAll("[^0-9]", "");
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


    private String getClusterFamilyName(File root)
    {
        String family = "";
        for (File f : root.listFiles())
        {
            if (f.getName().matches(GBK_FILE_REGEX))
            {
                return f.getName().replaceAll("\\.(cluster)(.*)(\\.gbk)", "");
            }
        }
        return family;
    }


    /**
     * Retrieves the Known Cluster data from the knowncluster folder of each ZIP
     * file entry loaded to the application and sets the associated data to each
     * respective BGC object
     *
     * @param knownClusterList A List of KnownCluster objects that hold the Known Cluster
     *                         Data of all the BGCs loaded in the application.
     */

    public void populateKnownClusterData(List<KnownCluster> knownClusterList, ApplicationBgcData appData)
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


    /**
     * Creates the Cluster Blast objects scanning all the zip files loaded into
     * the application and triggers its population at the end of the function.
     */

    public void setClusterBlastData(ApplicationBgcData appData, Workspace workspace)
    {

        List<ClusterBlast> clusterBlastList = new ArrayList<>();

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

        for (File parent : workspace.getRoot().listFiles())
        {
            if (parent.isDirectory())
            {
                File folder = new File(
                    uploadPath + workspace.getName() + "/" + parent.getName(),
                    FOLDER_NAME_CB);
                if (!folder.exists())
                {
                    return;
                }
                for (File file : folder.listFiles())
                {
                    if (file.getName().matches(FILE_REGEXP))
                    {
                        Scanner scanner = null;
                        try
                        {
                            scanner = new Scanner(file);

                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                        String[] tokens = scanner.nextLine().split(" ");
                        String origin = tokens[tokens.length - 1];
                        String number = file.getName().replaceAll("[^0-9]", "");
                        clusterBlastList.add(new ClusterBlast(file, origin, number));
                    }
                }
            }
        }

        // FILTER OUT ONLY THE CLUSTERS THAT ARE NEEDED

        Iterator<ClusterBlast> it = clusterBlastList.iterator();
        while (it.hasNext())
        {
            ClusterBlast cbe = it.next();
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

        populateClusterBlastData(clusterBlastList, appData);
    }


    /**
     * Retrieves the Cluster Blast data from the clusterblast folder of each ZIP
     * file entry loaded to the application and sets the associated data to each
     * respective BGC object
     *
     * @param clusterBlastList A List of ClusterBlast objects that hold the Cluster Blast
     *                         Data of all the BGCs loaded in the application.
     */

    public void populateClusterBlastData(List<ClusterBlast> clusterBlastList, ApplicationBgcData appData)
    {

        for (ClusterBlast cbe : clusterBlastList)
        {
            cbe.setCbLin(new ArrayList<>());
            Scanner scanner = null;
            try
            {
                scanner = new Scanner(cbe.getFile());
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (nextLine.trim().matches(HITS_REGEXP))
                {
                    break;
                }
            }
            int counter = 0;
            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (!(nextLine.trim()).matches(DETAILS_REGEXP) && !nextLine.trim().matches("") && counter < 99)
                {
                    counter++;
                    String[] tokens = nextLine.split("\\t");
                    cbe.getCbLin().add(new ClusterBlastLineage(tokens[0].split(" ")[1]));
                }
                else
                {
                    break;
                }
            }
            onlineResourceService.getClustersLineage(cbe);
            appData.getCluster(cbe.getClusterId()).setClusterBlastsData(cbe);
            LOG.info("Tree of Life for Cluster: " + cbe.getClusterId() + " constructed...");
        }
    }


    /**
     * Deletes the specified file if exists
     *
     * @param file the File to delete
     */

    public void delete(File file)
    {
        if (file.isDirectory())
        {
            if (file.list().length == 0)
            {
                file.delete();
            }
            else
            {
                String files[] = file.list();
                for (String temp : files)
                {
                    File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if (file.list().length == 0)
                {
                    file.delete();
                }
            }
        }
        else
        {
            file.delete();
        }
    }
}
