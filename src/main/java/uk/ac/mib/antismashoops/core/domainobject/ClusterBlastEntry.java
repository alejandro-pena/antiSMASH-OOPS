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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ClusterBlastEntry {
	private File file;
	private String recordName;
	private int recordNumber;
	private List<ClusterBlastLineage> cbLin = new ArrayList<>();
	private Document xmlLifeTree;

	private static final Logger logger = LoggerFactory.getLogger(ClusterBlastEntry.class);

	public ClusterBlastEntry(File file, String recordName, int recordNumber) {
		this.file = file;
		this.recordName = recordName;
		this.recordNumber = recordNumber;
	}

	public int getDiversityScore() {
		return getTreeSize(this.xmlLifeTree);
	}

	public int getTreeSize(Node root) {
		if (root == null)
			return 0;

		if (!root.hasChildNodes())
			return 1;

		int count = 0;
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			count += getTreeSize(children.item(i));
		}
		return count + 1;
	}

	public void generateLineageTree() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element element = doc.createElement("root");
			doc.appendChild(element);

			Node root = doc.getFirstChild();

			for (ClusterBlastLineage item : this.getCbLin()) {
				Node current = root;
				for (String species : item.getLineage()) {
					NodeList children = current.getChildNodes();
					boolean found = false;
					for (int i = 0; i < children.getLength(); i++) {
						if (children.item(i).getNodeName().equalsIgnoreCase(species)) {
							current = children.item(i);
							found = true;
							break;
						}
					}
					if (!found) {
						Element e = doc.createElement(species);
						current.appendChild(e);
						current = e;
					}
				}
			}

			this.xmlLifeTree = doc;
			prettyPrint(doc);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void prettyPrint(Document doc) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			// System.out.println(this.getRecordName() + " " +
			// this.getRecordNumber());
			// System.out.println(xmlString);
			writeToFile(xmlString);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(String data) {
		File directory = new File("lifeTreeOutput");
		File file = new File(directory, this.recordName + ".cluster" + this.recordNumber + ".xml");

		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (file.exists())
			file.delete();

		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}

	public List<ClusterBlastLineage> getCbLin() {
		return cbLin;
	}

	public void setCbLin(List<ClusterBlastLineage> cbLin) {
		this.cbLin = cbLin;
	}

	public Document getXmlLifeTree() {
		return xmlLifeTree;
	}

	public void setXmlLifeTree(Document xmlLifeTree) {
		this.xmlLifeTree = xmlLifeTree;
	}

	@Override
	public String toString() {
		return "ClusterBlastEntry [file=" + file + ", recordName=" + recordName + ", recordNumber=" + recordNumber
				+ "]\n";
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
