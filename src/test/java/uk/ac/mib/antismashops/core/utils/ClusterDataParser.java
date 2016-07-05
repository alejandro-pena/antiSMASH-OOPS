package uk.ac.mib.antismashops.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.MvcConfiguration;
import uk.ac.mib.antismashoops.core.model.Gene;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { AntiSmashOopsApplication.class, MvcConfiguration.class })
@WebAppConfiguration
public class ClusterDataParser
{
	private static final Logger logger = LoggerFactory.getLogger(ClusterDataParser.class);
	private static final String GENE_REGEXP = "(.+)gene(.+)(\\d+\\.\\.\\d+|complement\\(\\d+\\.\\.\\d+\\))";
	private static final String GENEID_REGEXP = "(.+)\\/db_xref=\"GeneID:\\d+\"";
	private static final String GENESYN_REGEXP = "(.+)\\/gene_synonym=\"(.+)\"";

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Test
	public void main() throws FileNotFoundException
	{
		logger.info("Main method started...");

		for (int i = 1; i < 10; i++)
		{
			logger.info("");
			logger.info("");
			logger.info("/************ FILE # " + i + "************/");
			File file = new File(uploadPath, "NC_003888.3.cluster00" + i + ".gbk");
			new ClusterDataParser().getGenesData(file);
		}
	}

	public List<Gene> getGenesData(File file) throws FileNotFoundException
	{
		List<Gene> geneList = new ArrayList<>();
		String geneId = "";
		String geneSynonym = "";
		int startBase;
		int stopBase;
		boolean complement = false;

		Scanner scanner = new Scanner(file);
		int geneCounter = 0;

		while (scanner.hasNextLine())
		{
			String token = scanner.nextLine();

			if (token.matches(GENE_REGEXP))
			{
				if (token.contains("complement"))
					complement = true;
				else
					complement = false;

				token = token.replaceAll("[A-Za-z]|\\(|\\)", "");
				String[] tokens = token.split("\\.\\.");
				startBase = Integer.parseInt(tokens[0].trim());
				stopBase = Integer.parseInt(tokens[1].trim());

				token = scanner.nextLine();

				if (token.matches(GENEID_REGEXP))
				{
					geneId = token.replaceAll("[^0-9]", "");
					scanner.nextLine();
					token = scanner.nextLine();

					if (token.matches(GENESYN_REGEXP))
					{
						geneSynonym = token.substring(token.indexOf('"') + 1, token.length() - 1);
						geneCounter++;
					}
				}

				geneList.add(new Gene(geneId, geneSynonym, startBase, stopBase, complement, ""));
			}

		}

		for (Gene g : geneList)
		{
			logger.info(g.toString());
		}

		logger.info("" + geneCounter);

		scanner.close();
		return geneList;
	}

}
