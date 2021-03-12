package com.github.ateranimavis.tiny_mcp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.ZipOutputStream;

import com.github.ateranimavis.mapping.IMappingProvider;
import com.github.ateranimavis.mapping.Mappings;
import com.github.ateranimavis.utils.Zip;
import net.minecraftforge.srgutils.IMappingFile;

public class Main {

    protected static final Path GENERATED = Paths.get(".generated");

    private static final String NL = "\r\n";

    public static void main(String[] args) throws Exception {
        generate(args, (mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild) -> {
            generate(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild, true);
            generate(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild, false);
        });
    }

    protected static void generate(String[] args, IGenerator generator) throws Exception {
        // MCPConfig
        String mcpType = args.length > 0 ? args[0] : ask("MCPConfig Type", "release");
        String mcpVersion = args.length > 1 ? args[1] : ask("MCPConfig Version", "1.16.4");

        // Mappings
        String mappingsChannel = args.length > 2 ? args[2] : ask("Mappings Channel", "snapshot");
        String mappingsVersion = args.length > 3 ? args[3] : ask("Mappings Version", "20201028-1.16.3");

        // Yarn
        String yarnVersion = args.length > 4 ? args[4] : ask("Yarn Version", "1.16.4");
        String yarnBuild = args.length > 5 ? args[5] : ask("Yarn Build", "9");

        Files.createDirectories(GENERATED);
        generator.accept(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild);
    }

    protected static void generate(
        String mcpType, String mcpVersion,
        String mappingsChannel, String mappingsVersion,
        String yarnVersion, String yarnBuild, boolean isV2
    ) throws IOException {
        Path path = GENERATED.resolve("mappings" + (isV2 ? "-v2" : "")  + ".jar");

        try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(path.toFile()))) {
            Writer writer = new OutputStreamWriter(stream);

            Zip.writeFile(stream, writer,"META-INF/MANIFEST.MF", () -> createManifest(
                mcpType, mcpVersion,
                mappingsChannel, mappingsVersion,
                yarnVersion, yarnBuild, isV2,
                writer)
            );

            Zip.writeFile(stream, writer,"mappings/mappings.tiny", () -> run(
                mcpType, mcpVersion,
                mappingsChannel, mappingsVersion,
                yarnVersion, yarnBuild, isV2,
                writer)
            );
        }
    }

    private static void createManifest(
        String mcpType, String mcpVersion,
        String mappingsChannel, String mappingsVersion,
        String yarnVersion, String yarnBuild, boolean isV2,
        Writer writer
    ) throws IOException {
        writer.append("Manifest-Version: 1.0").append(NL);
        writer.append("MCPConfig-Version: ").append(mcpType).append("-").append(mcpVersion).append(NL);
        writer.append("Mappings-Version: ").append(mappingsChannel).append("-").append(mappingsVersion).append(NL);
        writer.append("Yarn-Version: ").append(yarnVersion).append("+build.").append(yarnBuild).append(NL);
        writer.append("Tiny-Version: ").append(isV2 ? "2" : "1").append(NL);
    }

    private static String ask(String message, String fallback) {
        System.out.print(message + (fallback == null ? "" : " (or blank for " + fallback + ")") + ": ");
        String result = new Scanner(System.in).nextLine().trim();
        return result.isEmpty() ? fallback : result;
    }

    private static void run(
        String mcpType, String mcpVersion,
        String mappingsChannel, String mappingsVersion,
        String yarnVersion, String yarnBuild, boolean isV2,
        Writer writer
    ) throws IOException {
        IMappingProvider srg = Mappings.readSrg(mcpType, mcpVersion, mappingsChannel, mappingsVersion);
        IMappingProvider yarn = Mappings.readTiny(yarnVersion, yarnBuild, false);

        IMappingFile official2intermediary = yarn.get();
        IMappingFile intermediary2srg = official2intermediary.reverse().chain(srg.get());

        TinyRemapper.remapTiny(yarnVersion, yarnBuild, isV2, official2intermediary, intermediary2srg, writer);
    }

}
