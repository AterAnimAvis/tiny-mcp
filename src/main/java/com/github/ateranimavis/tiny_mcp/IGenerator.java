package com.github.ateranimavis.tiny_mcp;

import java.io.IOException;

@FunctionalInterface
public interface IGenerator {

    void accept(
        String mcpType, String mcpVersion,
        String mappingsChannel, String mappingsVersion,
        String yarnVersion, String yarnBuild
    ) throws IOException;

}
