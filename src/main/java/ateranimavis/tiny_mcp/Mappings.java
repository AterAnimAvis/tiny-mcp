package ateranimavis.tiny_mcp;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import net.minecraftforge.srgutils.IMappingFile;
import net.minecraftforge.srgutils.INamedMappingFile;
import net.minecraftforge.srgutils.IRenamer;

public class Mappings {

    public static IMappingFile readSrg(String srgUrl, String mcpUrl) throws IOException {
        IMappingFile srg = IMappingFile.load(Urls.download(srgUrl, "tsrg"));

        File mappings = Urls.download(mcpUrl, "zip");
        Map<String, String> fields = readCSV(Scanners.mappingsFields(mappings));
        Map<String, String> methods = readCSV(Scanners.mappingsMethods(mappings));

        return srg.rename(new IRenamer() {
            @Override
            public String rename(IMappingFile.IField value) {
                return fields.getOrDefault(value.getMapped(), value.getMapped());
            }

            @Override
            public String rename(IMappingFile.IMethod value) {
                return methods.getOrDefault(value.getMapped(), value.getMapped());
            }
        });
    }

    public static INamedMappingFile readTiny(String yarnUrl) throws IOException {
        return INamedMappingFile.load(getYarnMappings(yarnUrl));
    }

    public static File getYarnMappings(String yarnUrl) throws IOException {
        return Zip.extract(Urls.download(yarnUrl, "jar"), "mappings/mappings.tiny", "tiny");
    }

    public static IMappingFile getOfficial2Intermediary(INamedMappingFile mappings) {
        return mappings.getMap("official", "intermediary");
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

}
