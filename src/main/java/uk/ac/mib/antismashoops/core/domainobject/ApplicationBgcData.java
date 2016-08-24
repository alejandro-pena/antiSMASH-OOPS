package uk.ac.mib.antismashoops.core.domainobject;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.services.ExternalDataService;

@Component
@Scope("singleton")
public class ApplicationBgcData {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationBgcData.class);
	private static final List<BiosyntheticGeneCluster> bgcData;
	private static List<BiosyntheticGeneCluster> workingDataSet;

	static {
		bgcData = new ArrayList<>();
		workingDataSet = new ArrayList<>();
	}

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	@Autowired
	private ExternalDataService eds;

	public ApplicationBgcData() {
	}

	/**
	 * 
	 * Provides the BGC Application Data, if the data is in sync with the files
	 * uploaded the cached version will be provided. If not, a new scan and
	 * decompression will be done loading the new files or deleting the erased
	 * ones. This method clones the Application Data into the workingDataSet
	 * object to keep an intact copy of the data. The workingDataSet is a
	 * mutable object that changes according to every prioritisation request.
	 * 
	 * @return The workingDataSet cloned from the original data
	 * 
	 */

	public List<BiosyntheticGeneCluster> getBgcData() {

		if (eds.isBgcDataInSync(bgcData.size())) {
			ApplicationBgcData.workingDataSet.clear();
			for (BiosyntheticGeneCluster bgc : bgcData) {
				ApplicationBgcData.workingDataSet.add(bgc.clone());
			}
			logger.info("Cluster Data in sync...");
			return workingDataSet;
		}
		logger.info("Syncing Cluster Data...");
		eds.decompressLoadedFiles();
		eds.loadBggData(bgcData);
		ApplicationBgcData.workingDataSet.clear();
		for (BiosyntheticGeneCluster bgc : bgcData) {
			ApplicationBgcData.workingDataSet.add(bgc.clone());
		}
		return workingDataSet;
	}

	/**
	 * 
	 * Loads the BGC Data scores of the GC Content using a reference species and
	 * the Codon Bias
	 * 
	 * @param gcContentRef The Reference Species GC Content Percentage
	 * @param cutRef The CodonUsageTable object with the reference species data
	 * 
	 */

	public void loadBgcDataWithSpecies(Double gcContentRef, CodonUsageTable cutRef) {
		eds.populateClusterData(workingDataSet, gcContentRef, cutRef);
	}

	/**
	 * 
	 * Finds a specific BGC Object by the Cluster Id specified.
	 * 
	 * @param clusterId The BGC Id to look for in the workingDataSet
	 * 
	 * @return The Biosynthetic Gene Cluster requested
	 * 
	 */

	public BiosyntheticGeneCluster getCluster(String clusterId) {
		for (BiosyntheticGeneCluster c : workingDataSet) {
			if (c.getClusterId().equals(clusterId))
				return c;
		}
		return null;
	}

	/**
	 * 
	 * Getters and setters of the workingDataSet List object
	 * 
	 */

	public List<BiosyntheticGeneCluster> getWorkingDataSet() {
		return ApplicationBgcData.workingDataSet;
	}

	public void setWorkingDataSet(List<BiosyntheticGeneCluster> workingDataSet) {
		ApplicationBgcData.workingDataSet = workingDataSet;
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
