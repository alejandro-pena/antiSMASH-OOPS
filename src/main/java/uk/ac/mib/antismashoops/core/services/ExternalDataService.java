package uk.ac.mib.antismashoops.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.BlastHit;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastEntry;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastLineage;
import uk.ac.mib.antismashoops.core.domainobject.ClusterFw;
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
	private ClusterDataParser cdp;

	private static final String REGEX = "(.+)(cluster)(.*)(\\.gbk)";
	private static final String ZIP_REGEX = "(.+)(\\.zip)";
	private static final String FOLDER_NAME_KCB = "knownclusterblast";
	private static final String FOLDER_NAME_CB = "clusterblast";
	private static final String FILE_REGEXP = "(cluster)(.*)(\\.txt)";
	private static final String GENES_REGEXP = "Table of genes, locations, strands and annotations of query cluster:";
	private static final String HITS_REGEXP = "Significant hits:";
	private static final String DETAILS_REGEXP = "Details:";
	private static final String HITS_TABLE_REGEXP = "Table of Blast hits (query gene, subject gene, %identity, blast score, %coverage, e-value):";

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

		File root = new File(uploadPath);
		if (!root.exists())
			return;

		bgcData.clear();

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
			c.setGenes(cdp.getGenesData(c.getFile()));
			c.setClusterSequence(cdp.getClusterSequence(c.getFile()));
			c.setNumberOfGenes(c.getGenes().size());
			c.setCdsLength();
			c.setGcContent();
			c.setCodonUsageTable();
			c.setClusterType(cdp.getClusterType(c.getFile()));
		}
	}

	/**
	 * Populates the GC Content Score and the Codon Usage Score for the BGC Data
	 * 
	 * @param bgcData The populated List of the BGC Objects.
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

		for (File parent : new File(uploadPath).listFiles()) {
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

	public void setKnownClusterData(List<KnownCluster> knownClusterList) {
		knownClusterList.clear();

		for (File parent : new File(uploadPath).listFiles()) {
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

		populateKnownClusterData(knownClusterList);
	}

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

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (!(nextLine.trim()).matches(HITS_REGEXP) && !nextLine.trim().matches("")) {
					String[] tokens = nextLine.split("\\t");
					kce.getClusterGenes()
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

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(">>")) {
					ClusterFw c = new ClusterFw();

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
							c.getGeneList()
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

		}
	}

	public void setClusterBlastData(List<ClusterBlastEntry> clusterList, List<BiosyntheticGeneCluster> clusterData) {
		clusterList.clear();

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
						clusterList.add(new ClusterBlastEntry(file, origin, number));
					}
				}
			}
		}

		// FILTER OUT ONLY THE CLUSTERS THAT ARE NEEDED

		Iterator<ClusterBlastEntry> it = clusterList.iterator();
		while (it.hasNext()) {
			ClusterBlastEntry cbe = it.next();
			boolean found = false;
			for (BiosyntheticGeneCluster c : clusterData) {
				if (cbe.getClusterId().equals(c.getClusterId())) {
					found = true;
					break;
				}
			}
			if (!found)
				it.remove();
		}

		populateClusterBlastData(clusterList);
	}

	public void populateClusterBlastData(List<ClusterBlastEntry> clusterList) {
		for (ClusterBlastEntry cbe : clusterList) {
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

			for (ClusterBlastLineage cbl : cbe.getCbLin()) {

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				try {
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(new URL(
							"http://www.ebi.ac.uk/ena/data/view/" + cbl.getAccNumber() + "&display=xml&header=true")
									.openStream());

					NodeList taxons = doc.getElementsByTagName("taxon");
					for (int i = 0; i < taxons.getLength(); i++) {
						cbl.getLineage().add(
								filter(taxons.item(i).getAttributes().getNamedItem("scientificName").getTextContent()));
					}
				} catch (Exception e) {
					logger.error(e.getClass() + " - " + e.getMessage());
				}
				if (cbl.getLineage().size() > 0)
					cbl.getLineage().add(cbl.getLineage().remove(0));
			}
		}
	}

	private String filter(String s) {
		String filtered = s.replaceAll("(?!\\w|\\d|-|_|\\.).", "-");
		if (filtered.charAt(0) == '-')
			return filtered.substring(1);
		else
			return filtered;
	}

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
