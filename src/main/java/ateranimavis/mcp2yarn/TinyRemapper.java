package ateranimavis.mcp2yarn;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public interface TinyRemapper {

    String NL = "\r\n";

    static void remapTiny(String yarnUrl, Mappings official2intermediary, Mappings intermediary2srg, Writer writer) throws IOException {
        remap(Scanners.yarnMappings(yarnUrl), official2intermediary, intermediary2srg, writer);
    }

    static void remap(Scanner scanner, Mappings official2intermediary, Mappings intermediary2srg, Writer writer) throws IOException {
        String h = scanner.nextLine();
        String[] header = h.split("\t");
        Map<String, Integer> columns = new HashMap<>();

        for (int i = 1; i < header.length; i++) {
            columns.put(header[i], i - 1);
        }

        writer.append(h).append(NL);

        int fromColumn = columns.get("intermediary");
        int toColumn = columns.get("named");

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t");
            switch (line[0]) {
                case "CLASS": {
                    line[toColumn + 1] = getOrThrow(intermediary2srg.classes, line[fromColumn + 1]);
                    write(writer, line);
                    break;
                }

                case "FIELD": {
                    line[toColumn + 3] = getOrThrow(intermediary2srg.fields, getOrThrow(official2intermediary.classes, line[1]) + ":" + line[fromColumn + 3]).split(":")[1];
                    write(writer, line);
                    break;
                }

                case "METHOD": {
                    line[toColumn + 3] = getOrThrow(intermediary2srg.methods, getOrThrow(official2intermediary.classes, line[1]) + ":" + line[fromColumn + 3] + ":" + Mappings.remapMethodDescriptor(line[2], official2intermediary.classes)).split(":")[1];
                    write(writer, line);
                    break;
                }
            }
        }
        writer.flush();
    }

    static void write(Writer writer, String[] line) throws IOException {
        writer.append(line[0]);
        for (int i = 1; i < line.length; i++) writer.append("\t").append(line[i]);
        writer.append(NL);
    }

    static String getOrThrow(Map<String, String> mappings, String key) {
        if (!mappings.containsKey(key)) throw new AssertionError("Mapping Mismatch for " + key);
        return mappings.get(key);
    }

}
