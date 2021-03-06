package uk.ac.mib.antismashoops.core.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlast;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastLineage;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable.Detail;
import uk.ac.mib.antismashoops.core.domainobject.Species;

@Component
@Slf4j
public class OnlineResourceService
{
    @Value("${kazusa.gccontent.species.url}")
    private String kazusaGcContentUrl;

    @Value("${kazusa.species.search.url}")
    private String kazusaSpeciesSearchUrl;

    @Value("${kazusa.request.timeout}")
    private int kazusaRequestTimeout;


    /**
     * Access the www.kazusa.or.jp website to retrieve the GC Content of the
     * Specified Species
     *
     * @param speciesId The species requested
     * @return The GC Content percentage of the input species
     * @throws IOException Throws Input Output Exception
     */

    public double getGcContentBySpecies(String speciesId) throws IOException
    {
        String url = kazusaGcContentUrl.replace("{speciesId}", speciesId);

        log.info("Calling URL: {} for getting GC Content by species", url);

        Document doc = Jsoup
            .connect(url)
            .timeout(kazusaRequestTimeout)
            .get();

        Element bodyPage = doc.select("body").first();
        String codingGC = bodyPage.ownText().substring(10, 15);

        return Double.parseDouble(codingGC);
    }


    /**
     * Access the www.kazusa.or.jp website to retrieve the Usage Table of the
     * Specified Species
     *
     * @param speciesId The species requested
     * @return The Codon Usage Table object populated with the species
     * information
     * @throws IOException Throws Input Output Exception
     */

    public CodonUsageTable getSpeciesUsageTable(String speciesId) throws IOException
    {
        String url = kazusaGcContentUrl.replace("{speciesId}", speciesId);

        log.info("Calling URL: {} for getting Codon Usage Table", url);

        CodonUsageTable cu = new CodonUsageTable();
        LinkedHashMap<String, Detail> table = cu.getUsage();

        Document doc = Jsoup
            .connect(url)
            .timeout(kazusaRequestTimeout)
            .get();

        Element usageTable = doc.select("pre").first();
        Element speciesName = doc.select("i").first();

        cu.setSpecies(speciesName.html());

        String[] codons = usageTable.html().split("\\n");
        codons[0] = "";

        // POPULATE THE CODON USAGE TABLE (number of codons and frequency /1000)

        for (String s : codons)
        {
            if (!s.trim().equalsIgnoreCase(""))
            {
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


    /**
     * Sets the ClusterBlastLineage for the specified ClusterBlast object. Takes
     * the Accession Number previously populated and queries the lineage of that
     * cluster species to the www.ebi.ac.uk website.
     *
     * @param cbe The cluster blast entry where the List of Accession Numbers is
     *            stored
     */

    public void getClustersLineage(ClusterBlast cbe)
    {

        for (ClusterBlastLineage cbl : cbe.getCbLin())
        {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try
            {
                db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(
                    new URL("http://www.ebi.ac.uk/ena/data/view/" + cbl.getAccNumber() + "&display=xml&header=true")
                        .openStream());

                NodeList taxons = doc.getElementsByTagName("taxon");
                for (int i = 0; i < taxons.getLength(); i++)
                {
                    cbl.getLineage().add(
                        filterXML(taxons.item(i).getAttributes().getNamedItem("scientificName").getTextContent()));
                }
            }
            catch (Exception e)
            {
                log.error(e.getClass() + " - " + e.getMessage());
            }
            if (cbl.getLineage().size() > 0)
            {
                cbl.getLineage().add(cbl.getLineage().remove(0));
            }
        }
    }


    /**
     * Access the www.kazusa.or.jp website to retrieve the list of species
     * matching the specified string.
     *
     * @param species The species requested
     * @return A list of Species object with the matching species
     * @throws IOException Throws Input Output Exception
     */

    public List<Species> getSpeciesByName(String species) throws IOException
    {
        String url = kazusaSpeciesSearchUrl.replace("{keyword}", species);

        List<Species> speciesList = new ArrayList<>();

        Document doc = Jsoup
            .connect(url)
            .timeout(kazusaRequestTimeout)
            .get();

        Elements links = doc.select("a[href]");
        if (links.size() == 1)
        {
            speciesList.add(new Species("No data found.", "No data found.", "No data found."));
            return speciesList;
        }

        links.remove(links.size() - 1);

        for (Element link : links)
        {
            String id = link.attr("href").split("=")[1];
            speciesList.add(new Species(id, link.text().substring(0, link.text().indexOf('[') - 1), ""));
        }
        return speciesList;
    }


    /**
     * Access the www.kazusa.or.jp website to retrieve the list of species
     * matching the specified string formatting the list with special url data
     *
     * @param species The species requested
     * @return A list of Species object with the matching species
     * @throws IOException Java Input Output Exception
     */

    public List<Species> getSpeciesByNameForCodonUsage(String species) throws IOException
    {
        String url = kazusaSpeciesSearchUrl.replace("{keyword}", species);

        List<Species> speciesList = new ArrayList<>();

        Document doc = Jsoup
            .connect(url)
            .timeout(kazusaRequestTimeout)
            .get();

        Elements links = doc.select("a[href]");
        if (links.size() == 1)
        {
            speciesList.add(new Species("No data found.", "No data found.", "No data found."));
            return speciesList;

        }
        links.remove(links.size() - 1);
        for (Element link : links)
        {
            String id = link.attr("href").split("=")[1];
            speciesList.add(new Species(id, link.text(), "/codonUsage/show/" + id));
        }
        return speciesList;
    }


    /**
     * Filters a String to have only XML valid tag characters.
     *
     * @param s The string to filter
     * @return The string with the invalid characters changed to '-'
     */

    private String filterXML(String s)
    {
        String filtered = s.replaceAll("(?!\\w|\\d|-|_|\\.).", "-");
        if (filtered.charAt(0) == '-')
        {
            return filtered.substring(1);
        }
        else
        {
            return filtered;
        }
    }

}
