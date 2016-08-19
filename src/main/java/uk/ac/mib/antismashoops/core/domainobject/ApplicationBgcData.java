package uk.ac.mib.antismashoops.core.domainobject;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ApplicationBgcData {

	private static final List<BiosyntheticGeneCluster> bgcData;

	static {
		bgcData = new ArrayList<>();
	}

	public ApplicationBgcData() {
	}

	public static List<BiosyntheticGeneCluster> getBgcdata() {
		return bgcData;
	}
}
