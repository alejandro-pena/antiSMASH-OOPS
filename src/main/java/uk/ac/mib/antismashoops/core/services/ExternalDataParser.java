package uk.ac.mib.antismashoops.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.ac.mib.antismashoops.core.model.BlastHitEntry;
import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.ClusterBlastEntry;
import uk.ac.mib.antismashoops.core.model.ClusterBlastLineage;
import uk.ac.mib.antismashoops.core.model.ClusterFw;
import uk.ac.mib.antismashoops.core.model.Gene;
import uk.ac.mib.antismashoops.core.model.KnownClusterEntry;

@Component
public class ExternalDataParser
{
	private static final Logger logger = LoggerFactory.getLogger(ExternalDataParser.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	private static final String FOLDER_NAME_KCB = "knownclusterblast";
	private static final String FOLDER_NAME_CB = "clusterblast";
	private static final String FILE_REGEXP = "(cluster)(.*)(\\.txt)";
	private static final String GENES_REGEXP = "Table of genes, locations, strands and annotations of query cluster:";
	private static final String HITS_REGEXP = "Significant hits:";
	private static final String DETAILS_REGEXP = "Details:";
	private static final String HITS_TABLE_REGEXP = "Table of Blast hits (query gene, subject gene, %identity, blast score, %coverage, e-value):";

	public void setKnownClusterData(List<KnownClusterEntry> knownClusterList)
	{
		knownClusterList.clear();

		for (File parent : new File(uploadPath).listFiles())
		{
			if (parent.isDirectory())
			{
				File folder = new File(uploadPath + "/" + parent.getName(), FOLDER_NAME_KCB);
				if (!folder.exists())
					return;
				for (File file : folder.listFiles())
				{
					if (file.getName().matches(FILE_REGEXP))
					{
						Scanner scanner = null;
						try
						{
							scanner = new Scanner(file);

						} catch (FileNotFoundException e)
						{
							e.printStackTrace();
						}

						String[] tokens = scanner.nextLine().split(" ");
						String recordName = tokens[tokens.length - 1];
						int clusterNumber = Integer.parseInt(file.getName().replaceAll("[^0-9]", ""));
						knownClusterList.add(new KnownClusterEntry(file, recordName, clusterNumber));
					}
				}
			}
		}

		populateKnownClusterData(knownClusterList);
	}

	public void populateKnownClusterData(List<KnownClusterEntry> knownClusterList)
	{
		for (KnownClusterEntry kce : knownClusterList)
		{
			Scanner scanner = null;
			try
			{
				scanner = new Scanner(kce.getFile());

			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

			while (scanner.hasNextLine())
			{
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(GENES_REGEXP))
					break;
			}

			while (scanner.hasNextLine())
			{
				String nextLine = scanner.nextLine();
				if (!(nextLine.trim()).matches(HITS_REGEXP) && !nextLine.trim().matches(""))
				{
					String[] tokens = nextLine.split("\\t");
					kce.getClusterGenes()
							.add(new Gene(tokens[0], "", Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
									tokens[3].equals("+") ? true : false, tokens.length > 4 ? tokens[4] : ""));
				} else
					break;
			}

			while (scanner.hasNextLine())
			{
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(DETAILS_REGEXP) && scanner.hasNextLine())
					break;
			}

			while (scanner.hasNextLine())
			{
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(">>"))
				{
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

					while (scanner.hasNextLine())
					{
						nextLine = scanner.nextLine();
						if (!(nextLine.trim()).matches(HITS_TABLE_REGEXP) && !nextLine.trim().matches(""))
						{
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

					while (scanner.hasNextLine())
					{
						nextLine = scanner.nextLine();
						if (!(nextLine.trim()).matches(">>") && !nextLine.trim().matches(""))
						{
							tokens = nextLine.split("\\t|\\s");
							c.getBlastHits().add(new BlastHitEntry(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
									Integer.parseInt(tokens[3]), Double.parseDouble(tokens[4]), tokens[5]));
						} else
							break;
					}

					kce.getClusterHits().add(c);
				}

			}

		}
	}

	public void setClusterBlastData(List<ClusterBlastEntry> clusterList, List<Cluster> clusterData)
	{
		clusterList.clear();

		for (File parent : new File(uploadPath).listFiles())
		{
			if (parent.isDirectory())
			{
				File folder = new File(uploadPath + "/" + parent.getName(), FOLDER_NAME_CB);
				if (!folder.exists())
					return;
				for (File file : folder.listFiles())
				{
					if (file.getName().matches(FILE_REGEXP))
					{
						Scanner scanner = null;
						try
						{
							scanner = new Scanner(file);

						} catch (FileNotFoundException e)
						{
							e.printStackTrace();
						}

						String[] tokens = scanner.nextLine().split(" ");
						String recordName = tokens[tokens.length - 1];
						int clusterNumber = Integer.parseInt(file.getName().replaceAll("[^0-9]", ""));
						clusterList.add(new ClusterBlastEntry(file, recordName, clusterNumber));
					}
				}
			}
		}

		// FILTER OUT ONLY THE CLUSTERS THAT ARE NEEDED

		Iterator<ClusterBlastEntry> it = clusterList.iterator();
		while (it.hasNext())
		{
			ClusterBlastEntry cbe = it.next();
			boolean found = false;
			for (Cluster c : clusterData)
			{
				if (cbe.getRecordName().equalsIgnoreCase(c.getRecordName())
						&& cbe.getRecordNumber() == Integer.parseInt(c.getClusterNumber()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				it.remove();
		}

		populateClusterBlastData(clusterList);
	}

	public void populateClusterBlastData(List<ClusterBlastEntry> clusterList)
	{
		for (ClusterBlastEntry cbe : clusterList)
		{
			cbe.setCbLin(new ArrayList<>());

			Scanner scanner = null;
			try
			{
				scanner = new Scanner(cbe.getFile());

			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

			while (scanner.hasNextLine())
			{
				String nextLine = scanner.nextLine();
				if (nextLine.trim().matches(HITS_REGEXP))
					break;
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
				} else
					break;
			}

			for (ClusterBlastLineage cbl : cbe.getCbLin())
			{

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				try
				{
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(new URL(
							"http://www.ebi.ac.uk/ena/data/view/" + cbl.getAccNumber() + "&display=xml&header=true")
									.openStream());

					NodeList taxons = doc.getElementsByTagName("taxon");
					for (int i = 0; i < taxons.getLength(); i++)
					{
						cbl.getLineage().add(
								filter(taxons.item(i).getAttributes().getNamedItem("scientificName").getTextContent()));
					}
				} catch (ParserConfigurationException | SAXException | IOException e)
				{
					e.printStackTrace();
				}
				if (cbl.getLineage().size() > 0)
					cbl.getLineage().add(cbl.getLineage().remove(0));
			}
			System.out.println(cbe.getRecordName() + " " + cbe.getRecordNumber() + " processed!");
		}
	}

	private String filter(String s)
	{
		String filtered = s.replaceAll("(?!\\w|\\d|-|_|\\.).", "-");
		if (filtered.charAt(0) == '-')
			return filtered.substring(1);
		else
			return filtered;
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
