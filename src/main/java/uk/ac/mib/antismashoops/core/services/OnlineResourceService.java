package uk.ac.mib.antismashoops.core.services;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable.Detail;

@Component
public class OnlineResourceService {

	private static final Logger logger = LoggerFactory.getLogger(OnlineResourceService.class);

	public double getGcContentBySpecies(String refSpecies) throws IOException {
		Document doc = Jsoup
				.connect(
						"http://www.kazusa.or.jp/codon/cgi-bin/showcodon.cgi?species=" + refSpecies + "&aa=1&style=GCG")
				.timeout(15000).get();

		Element bodyPage = doc.select("body").first();
		String codingGC = bodyPage.ownText().substring(10, 15);

		return Double.parseDouble(codingGC);
	}

	public CodonUsageTable getSpeciesUsageTable(String refSpecies) throws IOException {
		CodonUsageTable cu = new CodonUsageTable();
		LinkedHashMap<String, Detail> table = cu.getUsage();

		Document doc = Jsoup
				.connect(
						"http://www.kazusa.or.jp/codon/cgi-bin/showcodon.cgi?species=" + refSpecies + "&aa=1&style=GCG")
				.timeout(15000).get();

		Element usageTable = doc.select("pre").first();
		Element speciesName = doc.select("i").first();

		cu.setSpecies(speciesName.html());

		String[] codons = usageTable.html().split("\\n");
		codons[0] = "";

		// POPULATE THE CODON USAGE TABLE (number of codons and frequency /1000)

		for (String s : codons) {
			if (!s.trim().equalsIgnoreCase("")) {
				String[] tmp = s.split("\\s+");
				CodonUsageTable.Detail d = table.get(tmp[1]);
				d.setCodonNumber((int) Double.parseDouble(tmp[2]));
				d.setFrequency(Double.parseDouble(tmp[3]));
			}
		}

		// POPULATE THE PERCENTAGE PER AMINO ACID
		cu.setUsagePerAminoacids();
		return cu;
	}

}
