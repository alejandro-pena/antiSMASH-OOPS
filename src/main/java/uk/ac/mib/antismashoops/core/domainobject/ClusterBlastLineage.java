package uk.ac.mib.antismashoops.core.domainobject;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ClusterBlastLineage {
	private String accNumber;
	private List<String> lineage = new ArrayList<>();

	public ClusterBlastLineage() {
	}

	/**
	 * Class Constructor. Calls the formatAccNumber function before assigning
	 * the value.
	 * 
	 * @param accNumber The NCBI accession number
	 */

	public ClusterBlastLineage(String accNumber) {
		this.accNumber = formatAccNumber(accNumber);
	}

	/**
	 * Formats the accession number deleting the versioning suffixes.
	 * 
	 * @param accNumber The NCBI accession number
	 * 
	 * @return The formatted accession number
	 */

	private String formatAccNumber(String accNumber) {
		if (!accNumber.contains("_"))
			return accNumber;
		else {
			String[] tokens = accNumber.split("_");
			if (tokens.length == 2)
				return tokens[0];
			if (tokens.length == 3)
				return tokens[1];
		}
		return "";
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public List<String> getLineage() {
		return lineage;
	}

	public void setLineage(List<String> lineage) {
		this.lineage = lineage;
	}
}
