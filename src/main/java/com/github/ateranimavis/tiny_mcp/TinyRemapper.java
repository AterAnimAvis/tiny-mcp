package com.github.ateranimavis.tiny_mcp;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.github.ateranimavis.utils.Scanners;
import net.minecraftforge.srgutils.IMappingFile;

public interface TinyRemapper {

    String NL = "\r\n";

    static void remapTiny(String yarnVersion, String yarnBuild, boolean generateV2, IMappingFile official2intermediary, IMappingFile intermediary2srg, Writer writer) throws IOException {
        remap(Scanners.yarnMappings(yarnVersion, yarnBuild, generateV2), official2intermediary, intermediary2srg, writer);
    }

    static void remap(Scanner scanner, IMappingFile official2intermediary, IMappingFile intermediary2srg, Writer writer) throws IOException {
        String h = scanner.nextLine();
        String[] header = h.split("\t");

        switch (header[0]) {
            case "tiny": {
                remapV2(scanner, h, header, intermediary2srg, writer);
                return;
            }
            case "v1": {
                remapV1(scanner, h, header, official2intermediary, intermediary2srg, writer);
                return;
            }
            default:
                throw new IllegalStateException("Unexpected Tiny Format");
        }
    }

    static void remapV2(Scanner scanner, String h, String[] header, IMappingFile intermediary2srg, Writer writer) throws IOException {
        // tiny	2	0	intermediary	named
        if (header.length != 5 || !Objects.equals(header[1], "2") || !Objects.equals(header[3], "intermediary") || !Objects.equals(header[4], "named")) {
            throw new IllegalStateException("Unexpected TinyV2 Format");
        }
        writer.append(h).append(NL);

        //for (IMappingFile.IClass clazz : intermediary2srg.getClasses()) {
        //    write(writer, new String[] { "c", clazz.getOriginal(), clazz.getMapped() });
        //
        //    for (IMappingFile.IMethod method : clazz.getMethods()) {
        //        write(writer, new String[] { "", "m", method.getDescriptor(), method.getOriginal(), method.getMapped() });
        //    }
        //
        //    for (IMappingFile.IField field : clazz.getFields()) {
        //        write(writer, new String[] { "", "f", field.getDescriptor(), field.getOriginal(), field.getMapped() });
        //    }
        //}

        IMappingFile.IClass clazz = null;

        while (scanner.hasNextLine()) {
            String scannerLine = scanner.nextLine();
            String[] line = scannerLine.split("\t");

            top:
            switch (line[0]) {
                case "c": {
                    clazz = intermediary2srg.getClass(line[1]);
                    line[2] = intermediary2srg.remapClass(line[1]);

                    write(writer, line);
                    break;
                }
                case "": {
                    switch (line[1]) {
                        case "f": {
                            assert clazz != null;

                            line[4] = clazz.remapField(line[3]);
                            write(writer, line);
                            break top;
                        }

                        case "m": {
                            assert clazz != null;

                            line[4] = clazz.remapMethod(line[3], line[2]);
                            write(writer, line);
                            break top;
                        }
                        case "c" :
                        case ""  : {
                            //TODO: Comments ["c"], Parameters ["", "p"]
                            writer.append(scannerLine);
                            writer.append(NL);
                            break top;
                        }
                    }
                }
                default: {
                    throw new IllegalStateException("Unhandled TinyV2 line: " + scannerLine + " " + Arrays.toString(line));
                }
            }
        }
        writer.flush();

    }

    static void remapV1(Scanner scanner, String h, String[] header, IMappingFile official2intermediary, IMappingFile intermediary2srg, Writer writer) throws IOException {
        Map<String, Integer> columns = new HashMap<>();

        for (int i = 1; i < header.length; i++) {
            columns.put(header[i], i - 1);
        }

        if (columns.get("intermediary") != 1 && columns.get("named") != 2) {
            throw new IllegalStateException("Unexpected TinyV1 Format");
        }

        // notch, intermediary, named
        //     0,            1,     2

        writer.append(h).append(NL);

        int fromColumn = 1; // columns.get("intermediary");
        int toColumn = 2; // columns.get("named");

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
                    throw new IllegalStateException("Unhandled TinyV1 line: " + scannerLine);
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

    static void write(Writer writer, String[] line, String prefix) throws IOException {
        writer.append(prefix);
        writer.append(line[0]);
        for (int i = 1; i < line.length; i++) writer.append("\t").append(line[i]);
        writer.append(NL);
    }

}
