package com.github.ateranimavis.mapping;

import java.io.IOException;
import java.nio.file.Path;

import com.github.ateranimavis.mapping.remote.RemoteMCPRenamer;
import com.github.ateranimavis.mapping.remote.RemoteSrgProvider;
import com.github.ateranimavis.mapping.remote.RemoteYarnProvider;
import com.github.ateranimavis.utils.Zip;

public class Mappings {

    public static IMappingProvider readSrg(String mcpType, String mcpVersion, String mappingsChannel, String mappingsVersion) {
        return new RenamedMappingsProvider(new RemoteSrgProvider(mcpType, mcpVersion), new RemoteMCPRenamer(mappingsChannel, mappingsVersion));
    }

    public static IMappingProvider readTiny(String yarnVersion, String yarnBuild, boolean isV2) {
        return new RemoteYarnProvider(yarnVersion, yarnBuild, isV2);
    }

    public static Path yarn(String yarnVersion, String yarnBuild, boolean isV2) throws IOException {
        String cache = ("yarn-{version}+build.{build}" + (isV2 ? "-v2" : "") + ".tiny")
            .replace("{version}", yarnVersion)
            .replace("{build}", yarnBuild);

        return Zip.extract(cache, MappingsDownload.yarn(yarnVersion, yarnBuild, isV2), "mappings/mappings.tiny");
    }
}
