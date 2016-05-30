package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.mib.antismashoops.core.model.Cluster;
import uk.ac.mib.antismashoops.core.utils.FileDataAnalyser;

@Controller
public class BasicParametersController
{
	private static final Logger logger = LoggerFactory.getLogger(BasicParametersController.class);

	@Autowired
	private FileDataAnalyser fda;

	@RequestMapping("/basicParameters")
	public String showResults(@RequestParam(value = "orderBy", required = false) String orderBy, ModelMap model)
			throws IOException
	{
		logger.info("Loading Basic Parameters View");

		List<Cluster> clusterData = fda.populateClusterObjects("");

		if (orderBy != null && !orderBy.equalsIgnoreCase(""))
		{
			switch (orderBy)
			{
			case "1":
				Collections.sort(clusterData, new Comparator<Cluster>() {
					@Override
					public int compare(Cluster c1, Cluster c2)
					{
						return c1.getName().compareTo(c2.getName());
					}
				});
				model.addAttribute("clusterData", clusterData);
				return "basicParameters";
			case "2":
				Collections.sort(clusterData);
				model.addAttribute("clusterData", clusterData);
				return "basicParameters";
			case "3":
				Collections.sort(clusterData, new Cluster());
				model.addAttribute("clusterData", clusterData);
				return "basicParameters";
			}
		}

		model.addAttribute("clusterData", clusterData);

		return "basicParameters";
	}
}
