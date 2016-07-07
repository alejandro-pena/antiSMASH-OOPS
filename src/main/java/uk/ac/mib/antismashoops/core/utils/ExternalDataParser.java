package uk.ac.mib.antismashoops.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.model.BlastHitEntry;
import uk.ac.mib.antismashoops.core.model.ClusterFw;
import uk.ac.mib.antismashoops.core.model.Gene;
import uk.ac.mib.antismashoops.core.model.KnownClusterEntry;

@Component
public class ExternalDataParser
{
	private static final Logger logger = LoggerFactory.getLogger(ExternalDataParser.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	private static final String FOLDER_NAME = "knownclusterblast";
	private static final String FILE_REGEXP = "(cluster)(.*)(\\.txt)";
	private static final String GENES_REGEXP = "Table of genes, locations, strands and annotations of query cluster:";
	private static final String GENES_SUBJECT_REGEXP = "Table of genes, locations, strands and annotations of subject cluster:";
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
				File folder = new File(uploadPath + "/" + parent.getName(), FOLDER_NAME);
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

		for (KnownClusterEntry kce : knownClusterList)
		{
			System.out.println(kce.getRecordName() + " - " + kce.getClusterNumber());
			System.out.println("");
			for (ClusterFw c : kce.getClusterHits())
			{
				System.out.println(c.getName() + " " + c.getSource() + " "
						+ ((c.getBlastHits().size() * 100 / kce.getClusterGenes().size()) * c.getBlastHitScore() / 100)
						+ "%");
			}

			System.out.println("");
			System.out.println("");
		}

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
