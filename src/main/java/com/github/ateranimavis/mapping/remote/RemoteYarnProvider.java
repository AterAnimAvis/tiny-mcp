package com.github.ateranimavis.mapping.remote;

import java.io.IOException;

import com.github.ateranimavis.mapping.base.BaseProvider;
import com.github.ateranimavis.mapping.Mappings;
import com.github.ateranimavis.utils.Lazy;
import net.minecraftforge.srgutils.IMappingFile;

public class RemoteYarnProvider extends BaseProvider {

    public RemoteYarnProvider(String yarnVersion, String yarnBuild, boolean isV2) {
        super(yarnVersion + "/" + yarnBuild + "/v" + (isV2 ? "2" : "1"), Lazy.of(() -> getYarn(yarnVersion, yarnBuild, isV2)));
    }

    public static IMappingFile getYarn(String yarnVersion, String yarnBuild, boolean isV2) throws IOException {
        return getNamedMappings(Mappings.yarn(yarnVersion, yarnBuild, isV2)).getMap("official", "intermediary");
    }

}
