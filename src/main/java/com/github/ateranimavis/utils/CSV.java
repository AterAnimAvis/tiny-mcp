package com.github.ateranimavis.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public interface CSV {

    static Map<String, String> readPairs(Scanner s) {
        Map<String, String> mappings = new LinkedHashMap<>();

        try (Scanner r = s) {
            r.nextLine();
            while (r.hasNextLine()) {
                String[] parts = r.nextLine().split(",");
                mappings.put(parts[0], parts[1]);
            }
        }

        s.close();
        return mappings;
    }

}
