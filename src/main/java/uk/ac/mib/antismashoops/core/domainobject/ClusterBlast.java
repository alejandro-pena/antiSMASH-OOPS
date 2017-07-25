package uk.ac.mib.antismashoops.core.domainobject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Slf4j
@Getter
@Setter
public class ClusterBlast
{
    private String clusterId;
    private File file;
    private String origin;
    private String number;
    private List<ClusterBlastLineage> cbLin = new ArrayList<>();
    private Document xmlLifeTree;


    /**
     * Class Constructor. Generates the ClusterBlast object for the BGC setting
     * the file source, origin, number and cluster id.
     *
     * @param file   The File object of the Cluster
     * @param origin The cluster family (zip file name)
     * @param number The cluster number of the family of BGCs
     */

    public ClusterBlast(File file, String origin, String number)
    {
        this.file = file;
        this.origin = origin;
        this.number = number;
        this.clusterId = this.origin + "-" + this.number;
    }


    /**
     * Gets the Diversity Score for this ClusterBlast data (xmlLifeTree size)
     *
     * @return The size of the associated xmlLifeTree
     */

    public int getDiversityScore()
    {
        return getTreeSize(this.xmlLifeTree);
    }


    /**
     * Counts the number of nodes of the specified DOM Tree.
     *
     * @param root The root node of the DOM Tree.
     * @return The number of nodes
     */

    public int getTreeSize(Node root)
    {
        if (root == null)
        {
            return 0;
        }

        if (!root.hasChildNodes())
        {
            return 1;
        }

        int count = 0;
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            count += getTreeSize(children.item(i));
        }
        return count + 1;
    }


    /**
     * Generates a Document tree object with the Lineage data from the Clusters
     * Blasted. Merges all the data in a single Tree of Life
     */

    public void generateLineageTree()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try
        {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element element = doc.createElement("root");
            doc.appendChild(element);

            Node root = doc.getFirstChild();

            for (ClusterBlastLineage item : this.getCbLin())
            {
                Node current = root;
                for (String species : item.getLineage())
                {
                    NodeList children = current.getChildNodes();
                    boolean found = false;
                    for (int i = 0; i < children.getLength(); i++)
                    {
                        if (children.item(i).getNodeName().equalsIgnoreCase(species))
                        {
                            current = children.item(i);
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                    {
                        Element e = doc.createElement(species);
                        current.appendChild(e);
                        current = e;
                    }
                }
            }

            this.xmlLifeTree = doc;
            prettyPrint(doc);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Generates the Tree of Life data in XML Format as a String and calls the
     * function to print it
     *
     * @param doc The root node of the Tree of Life data
     */

    public void prettyPrint(Document doc)
    {
        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            writeToFile(xmlString);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Writes the specified string to an xml file with the cluster family name
     * and number
     *
     * @param data The string that is going to be written. This string contains
     *             the data in XML format.
     */

    public void writeToFile(String data)
    {
        File directory = new File("lifeTreeOutput");
        File file = new File(directory, this.origin + ".cluster" + this.number + ".xml");

        if (!directory.exists())
        {
            directory.mkdirs();
        }
        if (file.exists())
        {
            file.delete();
        }

        try
        {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }
}
