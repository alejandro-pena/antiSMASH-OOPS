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
	private static final List<BiosyntheticGeneCluster> workingDataSet;

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

	public List<BiosyntheticGeneCluster> loadBgcDataWithSpecies(Double gcContentRef, CodonUsageTable cutRef) {
		eds.populateClusterData(workingDataSet, gcContentRef, cutRef);
		return workingDataSet;
	}

	public List<BiosyntheticGeneCluster> getWorkingDataSet() {
		return ApplicationBgcData.workingDataSet;
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
