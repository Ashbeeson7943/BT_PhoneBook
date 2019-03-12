package btPhoneBook;

import lombok.Data;

@Data
public class DataRecord {

    private String personsName;
    private String address1;
    private String address2;
    private String postcode;
    private String cli;
    private String currentProvider;

    public String writePretty() {
        return String.format("Name: %S\nAddress Line 1: %S\nAddress Line 2: %S\nPostcode: %S\nCLI: %S\nCurrent Provider: %S\n",
                personsName, address1, address2, postcode, cli, currentProvider);
    }

    public String toCSV() {
        return String.format("%S,%S,%S,%S,%S,%S", personsName, address1, address2, postcode, cli, currentProvider);
    }


}
