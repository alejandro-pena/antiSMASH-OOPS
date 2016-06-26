package uk.ac.mib.antismashops.core.utils;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.MvcConfiguration;
import uk.ac.mib.antismashoops.web.controller.DashboardController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { AntiSmashOopsApplication.class, MvcConfiguration.class })
@WebAppConfiguration
public class GenericTesting
{
	@Test
	public void test() throws IOException
	{
		DashboardController dc = new DashboardController();
		// dc.getSpeciesUsageTable("154034.mitochondrion");
	}
}
