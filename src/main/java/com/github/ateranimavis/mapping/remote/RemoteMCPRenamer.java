package com.github.ateranimavis.mapping.remote;

import java.io.IOException;

import com.github.ateranimavis.mapping.MappingsDownload;
import com.github.ateranimavis.mapping.base.BaseMCPRenamer;
import com.github.ateranimavis.utils.Lazy;
import net.minecraftforge.srgutils.IRenamer;

public class RemoteMCPRenamer extends BaseMCPRenamer {

    public RemoteMCPRenamer(String mappingsChannel, String mappingsVersion) {
        super(mappingsChannel + "-" + mappingsVersion, Lazy.of(() -> getSrgMcp(mappingsChannel, mappingsVersion)));
    }

    private static IRenamer getSrgMcp(String mappingsChannel, String mappingsVersion) throws IOException {
        return getRenamer(mappingsChannel + "-" + mappingsVersion, MappingsDownload.mcp(mappingsChannel, mappingsVersion));
    }

}
