package com.github.ateranimavis.mapping;

import com.github.ateranimavis.io.IOSupplier;
import net.minecraftforge.srgutils.IMappingFile;

public interface IMappingProvider extends IOSupplier<IMappingFile> {

    String describe();

    default boolean cachable() {
        return true;
    }

}
