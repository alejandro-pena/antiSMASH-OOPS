package uk.ac.mib.antismashops;

import javax.mail.MessagingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.core.services.FeedbackService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AntiSmashOopsApplication.class)
@WebAppConfiguration
public class AntiSmashOpsApplicationTests {

	@Autowired
	public FeedbackService fs;

	@Test
	public void applicationLoads() throws MessagingException {

		fs.sendFeedback("alexcpa.puma@gmail.com", "This is a test subject", "Feedback goes here!");

	}
}
