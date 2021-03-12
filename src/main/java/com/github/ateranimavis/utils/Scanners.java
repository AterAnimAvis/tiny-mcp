package com.github.ateranimavis.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import com.github.ateranimavis.mapping.Mappings;

public interface Scanners {

    static Scanner mappingsMethods(String description, Path mappings) throws IOException {
        return new Scanner(Zip.extract(description + "-methods.csv", mappings, "methods.csv"));
    }

    static Scanner mappingsFields(String description, Path mappings) throws IOException {
        return new Scanner(Zip.extract(description + "-fields.csv", mappings, "fields.csv"));
    }

    static Scanner yarnMappings(String yarnVersion, String yarnBuild, boolean generateV2) throws IOException {
        return new Scanner(Mappings.yarn(yarnVersion, yarnBuild, generateV2));
    }
}
