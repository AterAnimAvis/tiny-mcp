package com.github.ateranimavis.mapping;

import java.io.IOException;
import java.nio.file.Path;

import com.github.ateranimavis.utils.Urls;

public interface MappingsDownload {

    // NOTE: We use the commit before the change to official as the format is easier to work with in terms of applying mappings
    // MCP uses field_NUMBER_X_ the new format is f_NUMBER_ but re-indexed.
    String SRG          = "https://raw.githubusercontent.com/MinecraftForge/MCPConfig/0cdc6055297f0b30cf3e27e59317f229a30863a6/versions/{type}/{version}/joined.tsrg";
    String SRG_CACHE    = "mappings/joined-{type}-{version}.tsrg";

    String MCP          = "https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp_{channel}/{version}/mcp_{channel}-{version}.zip";
    String MCP_CACHE    = "mappings/mcp-{channel}-{version}.zip";

    String YARN         = "https://maven.fabricmc.net/net/fabricmc/yarn/{version}+build.{build}/yarn-{version}+build.{build}.jar";
    String YARN_CACHE   = "mappings/yarn-{version}+build.{build}.jar";

    String YARNv2       = "https://maven.fabricmc.net/net/fabricmc/yarn/{version}+build.{build}/yarn-{version}+build.{build}-v2.jar";
    String YARNv2_CACHE = "mappings/yarn-{version}+build.{build}-v2.jar";

    static Path mcp(String mappingsChannel, String mappingsVersion) throws IOException {
        String cache = MCP_CACHE.replace("{channel}", mappingsChannel).replace("{version}", mappingsVersion);
        String url = MCP.replace("{channel}", mappingsChannel).replace("{version}", mappingsVersion);
        return Urls.download(cache, url);
    }

    static Path srg(String mcpType, String mcpVersion) throws IOException {
        String cache = SRG_CACHE.replace("{type}", mcpType).replace("{version}", mcpVersion);
        String url = SRG.replace("{type}", mcpType).replace("{version}", mcpVersion);
        return Urls.download(cache, url);
    }

    static Path yarn(String yarn_version, String yarn_build, boolean v2) throws IOException {
        String cache = (v2 ? YARNv2_CACHE : YARN_CACHE).replace("{version}", yarn_version).replace("{build}", yarn_build);
        String url = (v2 ? YARNv2 : YARN).replace("{version}", yarn_version).replace("{build}", yarn_build);
        return Urls.download(cache, url);
    }

}
