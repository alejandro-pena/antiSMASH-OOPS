package uk.ac.mib.antismashoops.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.model.Gene;

@Component
public class ClusterDataParser
{

	private static final Logger logger = LoggerFactory.getLogger(ClusterDataParser.class);
	private static final String GENE_REGEXP = "(.+)gene(.+)(\\d+\\.\\.\\d+|complement\\(\\d+\\.\\.\\d+\\))";
	private static final String GENEID_REGEXP = "(.+)\\/db_xref=\"GeneID:\\d+\"";
	private static final String GENESYN_REGEXP = "(.+)\\/gene_synonym=\"(.+)\"";
	private static final String SEQUENCE_REGEXP = "a|g|c|t";

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	public List<Gene> getGenesData(File file)
	{
		List<Gene> geneList = new ArrayList<>();
		String geneId = "";
		String geneSynonym = "";
		int startBase;
		int stopBase;
		boolean complement = false;

		Scanner scanner = null;

		try
		{
			scanner = new Scanner(file);
		} catch (FileNotFoundException e)
		{
			logger.error(e.getMessage());
		}

		while (scanner.hasNextLine())
		{
			String token = scanner.nextLine();

			if (token.matches(GENE_REGEXP))
			{
				if (token.contains("complement"))
					complement = true;
				else
					complement = false;

				token = token.replaceAll("[A-Za-z]|\\(|\\)|<|>", "");
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
					}
				}

				geneList.add(new Gene(geneId, geneSynonym, startBase, stopBase, complement));

			}

		}

		scanner.close();
		return geneList;
	}

	public String getClusterSequence(File file)
	{
		Scanner scanner;
		StringBuilder sb = new StringBuilder();

		try
		{
			scanner = new Scanner(file);

			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				if (nextToken.equalsIgnoreCase("ORIGIN"))
					break;
			}

			scanner.useDelimiter("");

			while (scanner.hasNext())
			{
				String nextToken = scanner.next();
				if (nextToken.matches(SEQUENCE_REGEXP))
					sb.append(nextToken);
			}

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return sb.toString().toUpperCase();
	}

	public double getGcContent(Cluster cluster)
	{
		int count = 0;
		int total = 0;

		for (Gene g : cluster.getGenes())
		{
			String cds = cluster.getClusterSequence().substring(g.getStartBase() - 1, g.getStopBase());
			for (int i = 0; i < cds.length(); i++)
			{
				if (cds.charAt(i) == 'C' || cds.charAt(i) == 'G')
					count++;
			}
			total += cds.length();
		}

		return count * 100.0 / total;
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
