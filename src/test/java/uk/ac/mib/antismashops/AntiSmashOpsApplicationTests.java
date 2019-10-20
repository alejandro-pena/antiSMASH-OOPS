package uk.ac.mib.antismashops;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.mib.antismashoops.AntiSmashOopsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AntiSmashOopsApplication.class)
public class AntiSmashOpsApplicationTests
{

    @Test
    public void contextLoads()
    {
        Assert.assertTrue(true);
    }
}
