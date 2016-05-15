package uk.ac.mib.antismashops;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import uk.ac.mib.antismashoops.AntiSmashOpsApplication;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AntiSmashOpsApplication.class)
@WebAppConfiguration
public class AntiSmashOpsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
