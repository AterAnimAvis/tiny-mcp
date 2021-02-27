package ateranimavis.tiny_mcp;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public interface Scanners {

    static Scanner mappingsMethods(File mappings) throws IOException {
        return new Scanner(Zip.extract(mappings, "methods.csv", "csv"));
    }

    static Scanner mappingsFields(File mappings) throws IOException {
        return new Scanner(Zip.extract(mappings, "fields.csv", "csv"));
    }

    static Scanner yarnMappings(String yarnUrl) throws IOException {
        return new Scanner(Mappings.getYarnMappings(yarnUrl));
    }
}
