package ateranimavis.tiny_mcp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.minecraftforge.srgutils.IMappingFile;

public interface TinyRemapper {

    String NL = "\r\n";

    static void remapTiny(String yarnUrl, IMappingFile official2intermediary, IMappingFile intermediary2srg, Writer writer) throws IOException {
        remap(Scanners.yarnMappings(yarnUrl), official2intermediary, intermediary2srg, writer);
    }

    static void remap(Scanner scanner, IMappingFile official2intermediary, IMappingFile intermediary2srg, Writer writer) throws IOException {
        String h = scanner.nextLine();
        String[] header = h.split("\t");
        Map<String, Integer> columns = new HashMap<>();

        for (int i = 1; i < header.length; i++) {
            columns.put(header[i], i - 1);
        }

        if (columns.get("intermediary") != 1 && columns.get("named") != 2) {
            throw new IllegalStateException("Unexpected Tiny Format");
        }

        // notch, intermediary, named
        //     0,            1,     2

        writer.append(h).append(NL);

        int fromColumn = columns.get("intermediary");
        int toColumn = columns.get("named");

        while (scanner.hasNextLine()) {
            String scannerLine = scanner.nextLine();
            String[] line = scannerLine.split("\t");

            switch (line[0]) {
                case "CLASS": {
                    line[toColumn + 1] = intermediary2srg.remapClass(line[fromColumn + 1]);
                    write(writer, line);
                    break;
                }

                case "FIELD": {
                    IMappingFile.IClass clazz = intermediary2srg.getClass(official2intermediary.remapClass(line[fromColumn]));
                    line[toColumn + 3] = clazz.remapField(line[fromColumn + 3]);
                    write(writer, line);
                    break;
                }

                case "METHOD": {
                    IMappingFile.IClass clazz = intermediary2srg.getClass(official2intermediary.remapClass(line[fromColumn]));
                    line[toColumn + 3] = clazz.remapMethod(line[fromColumn + 3], official2intermediary.remapDescriptor(line[fromColumn + 1]));
                    write(writer, line);
                    break;
                }

                default: {
                    throw new IllegalStateException("Unhandled line: " + scannerLine);
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

}
