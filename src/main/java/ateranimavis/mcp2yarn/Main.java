package ateranimavis.mcp2yarn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final File GENERATED = new File(".generated");

    private static final String NL = "\r\n";

    public static void main(String[] args) throws Exception {
        // MCPConfig
        String mcpType = args.length > 0 ? args[0] : ask("MCPConfig Type", "release");
        String mcpVersion = args.length > 1 ? args[1] : ask("MCPConfig Version", "1.16.4");

        // Mappings
        String mappingsChannel = args.length > 2 ? args[2] : ask("Mappings Channel", "snapshot");
        String mappingsVersion = args.length > 3 ? args[3] : ask("Mappings Version", "20201028-1.16.3");

        // Yarn
        String yarnVersion = args.length > 4 ? args[4] : ask("Yarn Version", "1.16.4");
        String yarnBuild = args.length > 5 ? args[5] : ask("Yarn Build", "9");

        GENERATED.mkdirs();

        try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(new File(GENERATED, "mappings.jar")))) {
            Writer writer = new OutputStreamWriter(stream);

            Zip.writeFile(stream, writer,"META-INF/MANIFEST.MF", () -> createManifest(
                mcpType, mcpVersion,
                mappingsChannel, mappingsVersion,
                yarnVersion, yarnBuild,
                writer)
            );

            Zip.writeFile(stream, writer,"mappings/mappings.tiny", () -> run(
                mcpType, mcpVersion,
                mappingsChannel, mappingsVersion,
                yarnVersion, yarnBuild,
                writer)
            );
        }
    }

    private static void createManifest(
        String mcpType, String mcpVersion,
        String mappingsChannel, String mappingsVersion,
        String yarnVersion, String yarnBuild,
        Writer writer
    ) throws IOException {
        writer.append("Manifest-Version: 1.0").append(NL);
        writer.append("MCPConfig-Version: ").append(mcpType).append("-").append(mcpVersion).append(NL);
        writer.append("Mappings-Version: ").append(mappingsChannel).append("-").append(mappingsVersion).append(NL);
        writer.append("Yarn-Version: ").append(yarnVersion).append("+build.").append(yarnBuild).append(NL);
    }

    private static String ask(String message, String fallback) {
        System.out.print(message + (fallback == null ? "" : " (or blank for " + fallback + ")") + ": ");
        String result = new Scanner(System.in).nextLine().trim();
        return result.isEmpty() ? fallback : result;
    }

    private static void run(
        String mcpType, String mcpVersion,
        String mappingsChannel, String mappingsVersion,
        String yarnVersion, String yarnBuild,
        Writer writer
    ) throws IOException {
        String srgUrl  = Urls.srg(mcpType, mcpVersion);
        String mcpUrl  = Urls.mcp(mappingsChannel, mappingsVersion);
        String yarnUrl = Urls.yarn(yarnVersion, yarnBuild);

        run(srgUrl, mcpUrl, yarnUrl, writer);
    }

    private static void run(String srgUrl, String mcpUrl, String yarnUrl, Writer writer) throws IOException {
        Mappings srg = Mappings.readSrg(srgUrl, mcpUrl);
        Mappings official2intermediary =  Mappings.readTiny(yarnUrl);
        Mappings intermediary2srg = official2intermediary.invert().chain(srg);

        TinyRemapper.remapTiny(yarnUrl, official2intermediary, intermediary2srg, writer);
    }

}
