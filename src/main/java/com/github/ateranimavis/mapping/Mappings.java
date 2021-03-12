package com.github.ateranimavis.mapping;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

import com.github.ateranimavis.mapping.local.LocalMCPRenamer;
import com.github.ateranimavis.mapping.local.LocalProvider;
import com.github.ateranimavis.mapping.local.LocalYarnProvider;
import com.github.ateranimavis.mapping.remote.RemoteMCPRenamer;
import com.github.ateranimavis.mapping.remote.RemoteSrgProvider;
import com.github.ateranimavis.mapping.remote.RemoteYarnProvider;
import com.github.ateranimavis.utils.Zip;

public class Mappings {

    public static IMappingProvider readSrg(String mcpType, String mcpVersion, String mappingsChannel, String mappingsVersion) {
        IMappingProvider mapping = isLocal(mcpType)
            ? new LocalProvider(mcpVersion)
            : new RemoteSrgProvider(mcpType, mcpVersion);

        IRenamerProvider renamer  = isLocal(mappingsChannel)
            ? new LocalMCPRenamer(mappingsVersion)
            : new RemoteMCPRenamer(mappingsChannel, mappingsVersion);

        return new RenamedMappingsProvider(mapping, renamer);
    }

    public static IMappingProvider readTiny(String yarnVersion, String yarnBuild, boolean isV2) {
        return isLocal(yarnVersion)
            ? new LocalYarnProvider(yarnBuild)
            : new RemoteYarnProvider(yarnVersion, yarnBuild, isV2);
    }

    public static Path yarn(String yarnVersion, String yarnBuild, boolean isV2) throws IOException {
        String cache = ("yarn-{version}+build.{build}" + (isV2 ? "-v2" : "") + ".tiny")
            .replace("{version}", yarnVersion)
            .replace("{build}", yarnBuild);

        return Zip.extract(cache, MappingsDownload.yarn(yarnVersion, yarnBuild, isV2), "mappings/mappings.tiny");
    }

    private static boolean isLocal(String value) {
        return Objects.equals(value.toLowerCase(Locale.ROOT), "local");
    }

}
