package btPhoneBook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaceGenerator {

    private List<String> placeList = new ArrayList<>();
    private int count = 0;

    public PlaceGenerator(boolean testMode) {
        loadInFile(testMode);
    }

    private void loadInFile(boolean testMode) {
        BufferedReader reader;
        try {
            if (testMode) {
                reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separatorChar +
                        "placesSample.txt"));
            } else {
                reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separatorChar +
                        "places.txt"));
            }
            String line = reader.readLine();
            while (line != null) {
                placeList.add(line.trim());
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNext() {
        String place = placeList.get(count);
        count++;
        return place;
    }

    public int amountOfPlaces() {
        return placeList.size();
    }


}
