package ateranimavis.mcp2yarn;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Mappings {
    public final Map<String, String> classes = new LinkedHashMap<>();
    public final Map<String, String> fields = new LinkedHashMap<>();
    public final Map<String, String> methods = new LinkedHashMap<>();

    public Mappings chain(Mappings mappings) {
        Mappings result = new Mappings();

        classes.forEach((a, b) -> result.classes.put(a, mappings.classes.getOrDefault(b, b)));
        fields.forEach((a, b) -> result.fields.put(a, mappings.fields.getOrDefault(b, b)));
        methods.forEach((a, b) -> result.methods.put(a, mappings.methods.getOrDefault(b, b)));

        return result;
    }

    public Mappings invert() {
        Mappings result = new Mappings();

        classes.forEach((a, b) -> result.classes.put(b, a));
        fields.forEach((a, b) -> result.fields.put(b, a));
        methods.forEach((a, b) -> result.methods.put(b, a));

        return result;
    }

    public static Mappings readSrg(String srgUrl, String mcpUrl) throws IOException {
        File mappings = Urls.download(mcpUrl, "zip");

        Map<String, String> fields = readCSV(Scanners.mappingsFields(mappings));
        Map<String, String> methods = readCSV(Scanners.mappingsMethods(mappings));
        return readTSrg(Scanners.srgFile(srgUrl), fields, methods);
    }

    public static Mappings readTiny(String yarnUrl) throws IOException {
        return readTiny(Scanners.yarnMappings(yarnUrl), "official", "intermediary");
    }

    public static Mappings readTiny(Scanner scanner, String from, String to) {
        String[] header = scanner.nextLine().split("\t");
        Map<String, Integer> columns = new HashMap<>();

        for (int i = 1; i < header.length; i++) {
            columns.put(header[i], i - 1);
        }

        int fromColumn = columns.get(from);
        int toColumn = columns.get(to);

        Map<String, String> classes = new LinkedHashMap<>();
        Map<String, String> fields = new LinkedHashMap<>();
        Map<String, String> methods = new LinkedHashMap<>();

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t");
            switch (line[0]) {
                case "CLASS": {
                    classes.put(line[fromColumn + 1], line[toColumn + 1]);
                    break;
                }

                case "FIELD": {
                    fields.put(line[1] + ":" + line[fromColumn + 3], classes.get(line[1]) + ":" + line[toColumn + 3]);
                    break;
                }

                case "METHOD": {
                    methods.put(line[1] + ":" + line[fromColumn + 3] +  ":" + line[2], classes.get(line[1]) + ":" + line[toColumn + 3] + ":" + line[2]);
                    break;
                }
            }
        }

        scanner.close();
        return createMappings(classes, fields, methods);
    }

    public static Mappings readTSrg(Scanner scanner, Map<String, String> fieldNames, Map<String, String> methodNames) {
        Map<String, String> classes = new LinkedHashMap<>();
        Map<String, String> fields = new LinkedHashMap<>();
        Map<String, String> methods = new LinkedHashMap<>();

        String currentClassA = null;
        String currentClassB = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (!line.startsWith("\t")) {
                String[] parts = line.split(" ");
                classes.put(parts[0], parts[1]);
                currentClassA = parts[0];
                currentClassB = parts[1];
                continue;
            }

            line = line.substring(1);

            String[] parts = line.split(" ");

            if (parts.length == 2) {
                fields.put(currentClassA + ":" + parts[0], currentClassB + ":" + fieldNames.getOrDefault(parts[1], parts[1]));
            } else if (parts.length == 3) {
                methods.put(currentClassA + ":" + parts[0] +  ":" + parts[1], currentClassB + ":" + methodNames.getOrDefault(parts[2], parts[2]) + ":" + parts[1]);
            }
        }

        scanner.close();
        return createMappings(classes, fields, methods);
    }

    public static String remapMethodDescriptor(String method, Map<String, String> classMappings) {
        try {
            Reader r = new StringReader(method);
            StringBuilder result = new StringBuilder();
            boolean started = false;
            boolean insideClassName = false;
            StringBuilder className = new StringBuilder();
            while (true) {
                int c = r.read();
                if (c == -1) {
                    break;
                }

                if (c == ';') {
                    insideClassName = false;
                    result.append(classMappings.getOrDefault(className.toString(), className.toString()));
                }

                if (insideClassName) {
                    className.append((char) c);
                } else {
                    result.append((char) c);
                }

                if (c == '(') {
                    started = true;
                }

                if (started && c == 'L') {
                    insideClassName = true;
                    className.setLength(0);
                }
            }

            return result.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static Map<String, String> readCSV(Scanner s) {
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

    private static Mappings createMappings(Map<String, String> classes, Map<String, String> fields, Map<String, String> methods) {
        Mappings mappings = new Mappings();
        mappings.classes.putAll(classes);
        mappings.fields.putAll(fields);
        methods.forEach((a, b) -> mappings.methods.put(a, remapMethodDescriptor(b, classes)));
        return mappings;
    }

}
