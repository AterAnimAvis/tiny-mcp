package com.github.ateranimavis.tiny_mcp;

public class TinyV2 extends Main {

    public static void main(String[] args) throws Exception {
        generate(args, (mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild) ->
            generate(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild, true)
        );
    }

}
