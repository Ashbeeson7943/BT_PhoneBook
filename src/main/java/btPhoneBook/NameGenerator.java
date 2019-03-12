package btPhoneBook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NameGenerator {

    private List<String> nameList = new ArrayList<>();
    private int count = 0;
    private char[] initials = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private char[] testInitials = {'a', 'j'};

    public NameGenerator(boolean testMode) {
        loadInFile(testMode);
    }

    private void loadInFile(boolean testMode) {
        BufferedReader reader;
        try {
            if (testMode) {
                reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separatorChar +
                        "surnamesSample.txt"));
            } else {
                reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separatorChar +
                        "surnames.txt"));
            }
            String line = reader.readLine();
            while (line != null) {
                addNames(line.trim(), testMode);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNames(String line, boolean testMode) {
        char[] initialSet;
        if (testMode) {
            initialSet = testInitials;
        } else {
            initialSet = initials;
        }
        for (char c : initialSet) {
            nameList.add(c + " " + line);
        }
    }


    public String getNext() {
        String name = nameList.get(count);
        count++;
        return name;
    }

    public int amountOfNames() {
        return nameList.size();
    }

    public void nextPlace() {
        count = 0;
    }

}
