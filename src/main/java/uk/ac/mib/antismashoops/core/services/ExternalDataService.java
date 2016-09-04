package uk.ac.mib.antismashoops.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.domainobject.ApplicationBgcData;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.BlastHit;
import uk.ac.mib.antismashoops.core.domainobject.Cluster;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlast;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastLineage;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainobject.Gene;
import uk.ac.mib.antismashoops.core.domainobject.KnownCluster;
import uk.ac.mib.antismashoops.web.utils.ZipFileHandler;

@Component
public class ExternalDataService {
	private static final Logger logger = LoggerFactory.getLogger(ExternalDataService.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Autowired
	private ApplicationBgcData appData;

	@Autowired
	private OnlineResourceService ors;

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

	/**
	 * Decompresses the ZIP files loaded into the application. If no files are
	 * loaded no action is taken by the function.
	 */

	public void decompressLoadedFiles() {

		File root = new File(uploadPath);
		if (!root.exists())
			return;

		for (File parent : root.listFiles()) {
			if (parent.isFile() && parent.getName().matches(ZIP_REGEX)) {
				ZipFileHandler.decompressFile(parent, uploadPath);
			}
		}

		File __MACOSX = new File(uploadPath, "__MACOSX");
		if (__MACOSX.exists())
			this.delete(__MACOSX);
	}

	/**
	 * Constructs a BGC object per GBK file found in the folder or folders
	 * created after decompressing the ZIP file(s), passing to the constructor
	 * the File object pointing to the GBK source file. If the list contains any
	 * BGC object it will be cleared. Calls the initial data population.
	 *
	 * @param bgcData The empty or populated List of the BGC Objects.
	 */

	public void loadBggData(List<BiosyntheticGeneCluster> bgcData) {

		bgcData.clear();
		File root = new File(uploadPath);
		if (!root.exists())
			return;

		for (File parent : new File(uploadPath).listFiles()) {
			if (parent.isDirectory()) {
				File folder = new File(uploadPath, parent.getName());
				File[] files = folder.listFiles();
				for (File f : files) {
					if (f.getName().matches(REGEX)) {
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
	 *            attribute set to point their respective GBK file.
	 */

	private void populateClusterData(List<BiosyntheticGeneCluster> bgcData) {

		for (BiosyntheticGeneCluster c : bgcData) {
			c.setGenes(this.getGenesData(c.getFile()));
			c.setClusterSequence(this.getClusterSequence(c.getFile()));
			c.setNumberOfGenes(c.getGenes().size());
			c.setCdsLength();
			c.setGcContent();
			c.setCodonUsageTable();
			c.setClusterType(this.getClusterType(c.getFile()));
			c.setSpecies(this.getClusterSpecies(c.getFile()));
		}
	}

	/**
	 * Populates the GC Content Score and the Codon Usage Score for the BGC Data
	 * 
	 * @param bgcData The populated List of the BGC Objects.
	 * @param gcContentRef The GC Content of the reference species
	 * @param cutRef The Codon Usage Table object of the reference species
	 * 
	 */

	public void populateClusterData(List<BiosyntheticGeneCluster> bgcData, double gcContentRef,
			CodonUsageTable cutRef) {
		for (BiosyntheticGeneCluster c : bgcData) {
			c.setGcContentS(gcContentRef);
			c.setCodonBiasS(cutRef);
		}
	}

	/**
	 * Verifies if the number of GBK files loaded is the same as the number of
	 * BGC Objects in the Application Data BGC List.
	 * 
	 * @param bgcDataSize The number of BGCs in the Application Data.
	 * @return true if the size is the same and different from zero, if the
	 *         cluster list is empty or not in sync returns false.
	 */

	public boolean isBgcDataInSync(int bgcDataSize) {
		if (bgcDataSize == 0) {
			return false;
		}

		int zipFiles = 0;
		int directories = 0;
		int count = 0;

		File root = new File(uploadPath);
		File[] list;
		if (!root.exists()) {
			return false;
		} else {
			list = root.listFiles();
			if (list.length == 0) {
				return false;
			}
		}

		for (File parent : list) {
			if (parent.isDirectory()) {
				directories++;
				File folder = new File(uploadPath, parent.getName());
				File[] files = folder.listFiles();
				for (File f : files) {
					if (f.getName().matches(REGEX)) {
						count++;
					}
				}
			} else if (parent.getName().matches(ZIP_REGEX)) {
				zipFiles++;
			}
		}
		if (count == bgcDataSize && directories == zipFiles)
			return true;
		return false;
	}

	/**
	 * 
	 * Retrieves the Genes Data from a GBK cluster file. The gene data is a list
	 * of Gene Objects.
	 * 
	 * @param file the File object associated the BGC
	 * 
	 * @return An ArrayList of Gene objects
	 * 
	 */

	public List<Gene> getGenesData(File file) {
		List<Gene> geneList = new ArrayList<>();
		String geneId = "";
		String geneSynonym = "";
		int startBase;
		int stopBase;
		boolean complement = false;

		Scanner scanner = null;

		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}

		while (scanner.hasNextLine()) {
			String token = scanner.nextLine();

			if (token.matches(GENE_REGEXP)) {
				if (token.contains("complement"))
					complement = true;
				else
					complement = false;

				token = token.replaceAll("[A-Za-z]|\\(|\\)|<|>|_", "");
				String[] tokens = token.split("\\.\\.");
				startBase = Integer.parseInt(tokens[0].trim());
				stopBase = Integer.parseInt(tokens[1].trim());

				token = scanner.nextLine();

				if (token.matches(GENEID_REGEXP)) {
					geneId = token.replaceAll("[^0-9]", "");
					scanner.nextLine();
					token = scanner.nextLine();

					if (token.matches(GENESYN_REGEXP)) {
						geneSynonym = token.substring(token.indexOf('"') + 1, token.length() - 1);
					}
				}

				geneList.add(new Gene(geneId, geneSynonym, startBase, stopBase, complement, ""));

			}

		}

		scanner.close();
		return geneList;
	}

	/**
	 * 
	 * Retrieves the entire cluster sequence from a GBK file.
	 * 
	 * @param file the File object associated the BGC
	 * 
	 * @return A string with the nucleotide sequence in uppercase letters.
	 * 
	 */

	public String getClusterSequence(File file) {
		Scanner scanner;
		StringBuilder sb = new StringBuilder();

		try {
			scanner = new Scanner(file);

			while (scanner.hasNext()) {
				String nextToken = scanner.next();
				if (nextToken.equalsIgnoreCase("ORIGIN"))
					break;
			}

			scanner.useDelimiter("");

			while (scanner.hasNext()) {
				String nextToken = scanner.next();
				if (nextToken.matches(SEQUENCE_REGEXP))
					sb.append(nextToken);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString().toUpperCase();
	}

	/**
	 * 
	 * Retrieves the Cluster Type from a GBK cluster file.
	 * 
	 * @param file the File object associated the BGC
	 * 
	 * @return A string containing the type or types splitted by a hyphen
	 * 
	 */

	public String getClusterType(File file) {
		Scanner scanner;
		StringBuilder sb = new StringBuilder();

		try {
			scanner = new Scanner(file);
			String nextToken = "";

			while (scanner.hasNextLine()) {
				nextToken = scanner.nextLine();
				if (nextToken.matches(CLUSTER_REGEXP)) {
					break;
				}
			}

			while (scanner.hasNextLine()) {
				nextToken = scanner.nextLine();
				if (nextToken.matches(TYPE_REGEXP)) {
					sb.append(nextToken);
					break;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String type = sb.toString();
		type = sb.substring(sb.indexOf("\"") + 1, sb.lastIndexOf("\"")).trim();

		return type;
	}

	/**
	 * 
	 * Retrieves the Cluster Species from a GBK cluster file.
	 * 
	 * @param file the File object associated the BGC
	 * 
	 * @return A string containing species associated with the BGC
	 * 
	 */

	public String getClusterSpecies(File file) {
		Scanner scanner = null;
		String species = "";
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (scanner.hasNextLine())
			scanner.nextLine();
		if (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			String[] tokens = nextLine.split("DEFINITION");
			if (tokens.length > 0)
				species = tokens[1].trim();
		}
		return species;
	}

	/**
	 * 
	 * Creates the Known Cluster objects scanning all the zip files loaded into
	 * the application and triggers its population at the end of the function.
	 * 
	 */

	public void setKnownClusterData() {

		List<KnownCluster> knownClusterList = new ArrayList<>();

		File root = new File(uploadPath);
		File[] list;
		if (!root.exists()) {
			return;
		} else {
			list = root.listFiles();
			if (list.length == 0) {
				return;
			}
		}

		for (File parent : list) {
			if (parent.isDirectory()) {
				File folder = new File(uploadPath + "/" + parent.getName(), FOLDER_NAME_KCB);
				if (!folder.exists())
					return;
				for (File file : folder.listFiles()) {
					if (file.getName().matches(FILE_REGEXP)) {
						Scanner scanner = null;
						try {
							scanner = new Scanner(file);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						String[] tokens = scanner.nextLine().split(" ");
						String origin = tokens[tokens.length - 1];
						String number = file.getName().replaceAll("[^0-9]", "");
						knownClusterList.add(new KnownCluster(file, origin, number));
					}
				}
			}
		}

		Iterator<KnownCluster> it = knownClusterList.iterator();
		while (it.hasNext()) {
			KnownCluster cbe = it.next();
			boolean found = false;
			for (BiosyntheticGeneCluster c : appData.getWorkingDataSet()) {
				if (cbe.getClusterId().equals(c.getClusterId())) {
					found = true;
					break;
				}
			}
			if (!found)
				it.remove();
		}
		populateKnownClusterData(knownClusterList);
	}

	/**
	 * 
	 * Retrieves the Known Cluster data from the knowncluster folder of each ZIP
	 * file entry loaded to the application and sets the associated data to each
	 * respective BGC object
	 * 
	 * @param knownClusterList A List of KnownCluster objects that hold the
	 *            Known Cluster Data of all the BGCs loaded in the application.
	 */

	public void populateKnownClusterData(List<KnownCluster> knownClusterList) {
		for (KnownCluster kce : knownClusterList) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(kce.getFile());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(GENES_REGEXP))
					break;
			}

			// SET THE GENES OF THE QUERY CLUSTER (BGC)

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (!(nextLine.trim()).matches(HITS_REGEXP) && !nextLine.trim().matches("")) {
					String[] tokens = nextLine.split("\\t");
					kce.getBgcGenes()
							.add(new Gene(tokens[0], "", Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
									tokens[3].equals("+") ? true : false, tokens.length > 4 ? tokens[4] : ""));
				} else
					break;
			}

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(DETAILS_REGEXP) && scanner.hasNextLine())
					break;
			}

			// SET THE CLUSTER HITS FOR THE BGC

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(">>")) {
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

					while (scanner.hasNextLine()) {
						nextLine = scanner.nextLine();
						if (!(nextLine.trim()).matches(HITS_TABLE_REGEXP) && !nextLine.trim().matches("")) {
							tokens = nextLine.split("\\t");
							c.getQueryClusterGenes()
									.add(new Gene(tokens[1], "", Integer.parseInt(tokens[2]),
											Integer.parseInt(tokens[3]), tokens[4].equals("+") ? true : false,
											tokens.length > 5 ? tokens[5] : ""));
						} else
							break;
					}

					scanner.nextLine();

					// SET THE BLAST HITS FOR THE CLUSTER

					while (scanner.hasNextLine()) {
						nextLine = scanner.nextLine();
						if (!(nextLine.trim()).matches(">>") && !nextLine.trim().matches("")) {
							tokens = nextLine.split("\\t|\\s");
							c.getBlastHits().add(new BlastHit(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
									Integer.parseInt(tokens[3]), Double.parseDouble(tokens[4]), tokens[5]));
						} else
							break;
					}

					kce.getClusterHits().add(c);
				}

			}
			appData.getCluster(kce.getClusterId()).setKnownClustersData(kce);
		}
	}

	/**
	 * 
	 * Creates the Cluster Blast objects scanning all the zip files loaded into
	 * the application and triggers its population at the end of the function.
	 * 
	 */

	public void setClusterBlastData() {

		List<ClusterBlast> clusterBlastList = new ArrayList<>();

		File root = new File(uploadPath);
		File[] list;
		if (!root.exists()) {
			return;
		} else {
			list = root.listFiles();
			if (list.length == 0) {
				return;
			}
		}

		for (File parent : new File(uploadPath).listFiles()) {
			if (parent.isDirectory()) {
				File folder = new File(uploadPath + "/" + parent.getName(), FOLDER_NAME_CB);
				if (!folder.exists())
					return;
				for (File file : folder.listFiles()) {
					if (file.getName().matches(FILE_REGEXP)) {
						Scanner scanner = null;
						try {
							scanner = new Scanner(file);

						} catch (FileNotFoundException e) {
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
		while (it.hasNext()) {
			ClusterBlast cbe = it.next();
			boolean found = false;
			for (BiosyntheticGeneCluster c : appData.getWorkingDataSet()) {
				if (cbe.getClusterId().equals(c.getClusterId())) {
					found = true;
					break;
				}
			}
			if (!found)
				it.remove();
		}

		populateClusterBlastData(clusterBlastList);
	}

	/**
	 * 
	 * Retrieves the Cluster Blast data from the clusterblast folder of each ZIP
	 * file entry loaded to the application and sets the associated data to each
	 * respective BGC object
	 * 
	 * @param clusterBlastList A List of ClusterBlast objects that hold the
	 *            Cluster Blast Data of all the BGCs loaded in the application.
	 */

	public void populateClusterBlastData(List<ClusterBlast> clusterBlastList) {

		for (ClusterBlast cbe : clusterBlastList) {
			cbe.setCbLin(new ArrayList<>());
			Scanner scanner = null;
			try {
				scanner = new Scanner(cbe.getFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(HITS_REGEXP))
					break;
			}
			int counter = 0;
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (!(nextLine.trim()).matches(DETAILS_REGEXP) && !nextLine.trim().matches("") && counter < 99) {
					counter++;
					String[] tokens = nextLine.split("\\t");
					cbe.getCbLin().add(new ClusterBlastLineage(tokens[0].split(" ")[1]));
				} else
					break;
			}
			ors.getClustersLineage(cbe);
			appData.getCluster(cbe.getClusterId()).setClusterBlastsData(cbe);
			logger.info("Tree of Life for Cluster: " + cbe.getClusterId() + " constructed...");
		}
	}

	/**
	 * 
	 * Deletes the specified file if exists
	 * 
	 * @param file the File to delete
	 * 
	 */

	public void delete(File file) {
		if (file.isDirectory()) {
			if (file.list().length == 0)
				file.delete();
			else {
				String files[] = file.list();
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}
				if (file.list().length == 0)
					file.delete();
			}
		} else
			file.delete();
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
