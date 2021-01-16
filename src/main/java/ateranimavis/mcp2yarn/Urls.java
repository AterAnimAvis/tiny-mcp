package ateranimavis.mcp2yarn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public interface Urls {

    String SRG = "https://raw.githubusercontent.com/MinecraftForge/MCPConfig/master/versions/{mcp_type}/{mcp_version}/joined.tsrg";
    String MCP = "https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp_{mcp_channel}/{mcp_mapping}/mcp_{mcp_channel}-{mcp_mapping}.zip";
    String YARN = "http://maven.modmuss50.me/net/fabricmc/yarn/{yarn_version}+build.{yarn_build}/yarn-{yarn_version}+build.{yarn_build}.jar";

    static String srg(String mcp_type, String mcp_version) {
        return SRG.replace("{mcp_type}", mcp_type).replace("{mcp_version}", mcp_version);
    }

    static String mcp(String mcp_channel, String mcp_mapping) {
        return MCP.replace("{mcp_channel}", mcp_channel).replace("{mcp_mapping}", mcp_mapping);
    }

    static String yarn(String yarn_version, String yarn_build) {
        return YARN.replace("{yarn_version}", yarn_version).replace("{yarn_build}", yarn_build);
    }

    static File download(String url, String type) throws IOException {
        return Caching.cached(Hashing.hash(url), type, (file) -> downloadFileFromUrl(url, file));
    }

    static void downloadFileFromUrl(String url, File destination) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
