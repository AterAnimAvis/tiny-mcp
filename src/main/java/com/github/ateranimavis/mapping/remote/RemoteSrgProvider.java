package com.github.ateranimavis.mapping.remote;

import java.io.IOException;

import com.github.ateranimavis.mapping.MappingsDownload;
import com.github.ateranimavis.mapping.base.BaseProvider;
import com.github.ateranimavis.utils.Lazy;
import net.minecraftforge.srgutils.IMappingFile;

public class RemoteSrgProvider extends BaseProvider {

    public RemoteSrgProvider(String mcpType, String mcpVersion) {
        super(mcpType + "/" + mcpVersion, Lazy.of(() -> getSrg(mcpType, mcpVersion)));
    }

    public static IMappingFile getSrg(String mcpType, String mcpVersion) throws IOException {
        return getMappings(MappingsDownload.srg(mcpType, mcpVersion));
    }

}
