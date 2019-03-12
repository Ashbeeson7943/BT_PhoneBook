package btPhoneBook;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import talktalk.automation.logging.TestLogger;
import talktalk.automation.test.BaseTest;
import talktalk.automation.webbased.Pilot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//MUST
//TODO: Get past page 2
//TODO: Update City/Town List
//TODO: Generate file after evey place
//COULD
//TODO: Add available packages
//TODO: Add ALK Reference
public class BTPhoneBookCrawler extends BaseTest {

    private Pilot pilot;
    private List<DataRecord> records = new ArrayList<>();
    private static File recordCSV;
    private static NameGenerator nameGenerator;
    private static PlaceGenerator placeGenerator;
    private String currentLocation = "";

    @BeforeClass
    public static void init() {
        nameGenerator = new NameGenerator(true);
        placeGenerator = new PlaceGenerator(true);
    }

    @Before
    public void setUp() {
        pilot = new Pilot("chrome");
        pilot.manage().window().maximize();
        pilot.navigate().to("https://www.thephonebook.bt.com/person/");
    }

    @Test
    public void crawl() {
        boolean firstPass = true;
        TestLogger testLogger = getReport().addTest();
        pilot.navigate().to("https://www.thephonebook.bt.com/person/");
        pilot.findElement(By.cssSelector("body > div.cc_banner-wrapper > div > a.cc_btn.cc_btn_accept_all")).click();
        for (int i = 0; i < placeGenerator.amountOfPlaces(); i++) {
            currentLocation = placeGenerator.getNext();
            for (int j = 0; j < nameGenerator.amountOfNames(); j++) {

                if (!firstPass) {
                    pilot.navigate().to("https://www.thephonebook.bt.com/person/");
                }
                SearchForPerson();
                int intialPageCount = 2;
                pilot.pause(3000, TimeUnit.MILLISECONDS);
                List<WebElement> pageButtons = new ArrayList<>();
                findPageButtons(pageButtons, intialPageCount);
                int count = 1;
                for (WebElement page : pageButtons) {
                    System.out.println("Page: " + count + "\n------------------------------------------------------------------");
                    PullOutResults();
                    pilot.pause(1000, TimeUnit.MILLISECONDS);
                    try {
                        page.click();
                        count++;
                        intialPageCount++;
                        System.out.println("\n");
                    } catch (StaleElementReferenceException sere) {
                        break;
                    }
                }
                firstPass = false;
            }
            writeToCSV(currentLocation);
            records.clear();
            nameGenerator.nextPlace();
            //Create csv
        }
    }


    private void findPageButtons(List<WebElement> pageButtons, int initialPageCount) {
        pageButtons.clear();
        for (int i = initialPageCount; i < 5; i++) {
            try {
                pageButtons.add(pilot.findElement(By.cssSelector("#searchResultPaging > div.row.text-center.py-3 > div > a:nth-child(" + i + ")")));
            } catch (NoSuchElementException nsee) {
                break;
            }
        }
    }

    private void PullOutResults() {
        WebElement results = pilot.findElement(By.cssSelector("#result"));
        List<WebElement> detailContainers = new ArrayList<>();

        for (int i = 5; i < 30; i++) {
            try {
                detailContainers.add(pilot.findElement(By.cssSelector("#result > div:nth-child(" + i + ")")));
//            System.out.println(i);
            } catch (NoSuchElementException nsee) {
                break;
            }
        }
        results.findElements(By.cssSelector("div.mb-3 border border-dark px-3"));
        for (WebElement details : detailContainers) {
            String personsName = details.findElement(By.cssSelector("div:nth-child(1) > div")).getText().trim();
            String ad1;
            try {
                ad1 = details.findElement(By.cssSelector("div:nth-child(2) > div.col-8.py-2 > div > div:nth-child(1)")).getText().trim();
            } catch (NoSuchElementException nsee) {
                break;
            }
            String[] split1 = ad1.split(",");
            String address1;
            if (split1.length > 1) {
                address1 = split1[0] + " " + split1[1];
            } else {
                address1 = ad1;
            }
            String postcode = details.findElement(By.cssSelector("div:nth-child(2) > div.col-8.py-2 > div > div:nth-child(2)")).getText().trim();
            String cli = details.findElement(By.cssSelector("div:nth-child(3) > div.col-12.py-2.d-none.d-sm-flex > div.ml-3.d-inline.light-blue.my-auto.no-wrap > a")).getText().trim();
            cli = cleanUpCLI(cli);
            String[] split = postcode.split(" ");
            String address2 = "";
            if (split.length > 2) {
                address2 = split[0];
                postcode = split[1] + " " + split[2];
            }
            DataRecord dataRecord = new DataRecord();
            dataRecord.setPersonsName(personsName);
            dataRecord.setAddress1(address1);
            dataRecord.setAddress2(address2);
            dataRecord.setPostcode(postcode);
            dataRecord.setCli(cli);
            records.add(dataRecord);
            System.out.println(dataRecord.writePretty());

        }
    }

    private String cleanUpCLI(String cli) {
        if (cli.contains("(")) {
            cli = cli.replace("(", "");
            cli = cli.replace(")", "");
        }
        if (cli.contains(" ")) {
            cli = cli.replace(" ", "");
        }
        return cli;
    }


    private void writeToCSV(String currentLocation) {
        recordCSV = new File(System.getProperty("user.dir") + File.separatorChar + "BT_Records_" + currentLocation.toUpperCase() + "_" + System.currentTimeMillis() + ".csv");
        try (PrintWriter writer = new PrintWriter(recordCSV)) {
            writer.write("Name,Address Line 1,Address Line 2,Postcode,CLI,Current Provider\n");
            for (DataRecord dataRecord : records) {
                writer.write(dataRecord.toCSV() + "\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void SearchForPerson() {
        pilot.pause(3000, TimeUnit.MILLISECONDS);
        pilot.findElement(By.cssSelector("#Surname")).sendKeys(nameGenerator.getNext());
        pilot.pause(500, TimeUnit.MILLISECONDS);
        pilot.findElement(By.cssSelector("#locationTumblingListLocAuto")).sendKeys(currentLocation);
        pilot.pause(500, TimeUnit.MILLISECONDS);
        pilot.findElement(By.cssSelector("#btnSearchPerson")).click();
        pilot.pause(500, TimeUnit.MILLISECONDS);
    }

    @After
    public void tearDown() {
        pilot.quit();

    }
}
