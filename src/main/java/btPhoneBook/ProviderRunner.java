package btPhoneBook;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import talktalk.automation.test.BaseTest;
import talktalk.automation.test.ParallelSuite;

@RunWith(ParallelSuite.class)
@Suite.SuiteClasses({ProviderLookUp.class})
public class ProviderRunner extends BaseTest {

    @BeforeClass
    public static void init() {
        openReport("ProviderLookup");
    }

    @AfterClass
    public static void cleanUp() {
        closeReport();
    }

}
