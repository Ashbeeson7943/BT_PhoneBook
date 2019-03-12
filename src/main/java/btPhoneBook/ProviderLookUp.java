package btPhoneBook;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import talktalk.automation.test.BaseTest;
import talktalk.automation.webbased.Pilot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//TODO: read in BT csv
//TODO: find providers
//TODO: rename file
//TODO: save file
public class ProviderLookUp extends BaseTest {

    //File name regex = "\w*_\w*_\w*_\d*.csv"
    private static final String FILEREGEX = "[a-zA-Z]*_[a-zA-Z]*_[a-zA-Z]*_\\d*.csv";
    private static final String BASE_FILEPATH = System.getProperty("user.dir");

    private List<DataRecord> records = new ArrayList<>();
    private Pilot pilot;
    private String oldFileName;
    private int filesFound;
    private boolean atLeastOneFileExists = true;


    @Before
    public void setUp() {
        //TODO: make this a sys var
        File tmpFile = findFile();
        if (tmpFile != null) {
            oldFileName = tmpFile.getName();
            createDataRecords(tmpFile);
            tmpFile.delete();
        } else {
            atLeastOneFileExists = false;
        }
        pilot = new Pilot("chrome");
    }

    private File findFile() {
        File directory = new File(".");
        String pattern = FILEREGEX;
        FileFilter filter = new RegexFileFilter(pattern);
        File[] files = directory.listFiles();
        files = directory.listFiles(filter);
        System.out.println("Matching Files for Regex: " + pattern);
        for (File file : files) {
            System.out.println(file.getName());
        }

        if (files.length > 1) {
            filesFound = files.length;
            final File file = files[0];
            System.out.println("More than one file found\nUsing the first File: " + file.getAbsolutePath());
            return file;
        } else if (files.length == 1) {
            return files[0];
        } else {
            System.out.println("No files found");
            return null;
        }
    }

    private void createDataRecords(File tmpFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(tmpFile))) {
            String line = reader.readLine();
            while (line != null) {
                records.add(createData(line));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataRecord createData(String line) {
        //Name,Address Line 1,Address Line 2,Postcode,CLI,Current Provider
        final String[] values = line.split(",");
        DataRecord record = new DataRecord();
        record.setPersonsName(values[0]);
        record.setAddress1(values[1]);
        record.setAddress2(values[2]);
        record.setPostcode(values[3]);
        record.setCli(values[4]);
        return record;
    }

    @Test
    public void compileProviders() {
        if (atLeastOneFileExists) {
            for (DataRecord record : records) {
                try {
                    pilot.navigate().to("https://portal.aql.com/telecoms/network_lookup.php?number=01372724173&nlSubmit=submit");
                    WebElement input = pilot.findElement(By.cssSelector("#container > div.inner_yellow_band > div.inner_cont_holder > div.inner_content_mid > form > label > input[type=\"text\"]"));
                    input.clear();
                    input.sendKeys(record.getCli());
                    pilot.findElement(By.cssSelector("#container > div.inner_yellow_band > div.inner_cont_holder > div.inner_content_mid > form > div > input")).click();
                    //getResults
                    String provider = "";
                    WebElement resultsTable = pilot.findElement(By.cssSelector("#container > div.inner_yellow_band > div.inner_cont_holder > div.inner_content_mid > table"));
                    List<WebElement> rows = resultsTable.findElements(By.cssSelector("tr"));
                    for (WebElement row : rows) {
                        List<WebElement> td = row.findElements(By.cssSelector("td"));
                        String key = td.get(0).getText();
                        if (key.equalsIgnoreCase("Network")) {
                            provider = td.get(1).getText();
                        }
                    }
                    record.setCurrentProvider(provider);
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    @After
    public void tearDown() {
        pilot.quit();
        if (atLeastOneFileExists) {
            try (PrintWriter writer = new PrintWriter(new File(BASE_FILEPATH + File.separatorChar + "COMP_" + oldFileName))) {
                writer.write("Name,Address Line 1,Address Line 2,Postcode,CLI,Current Provider\n");
                for (DataRecord dataRecord : records) {
                    writer.write(dataRecord.toCSV() + "\n");
                }
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
