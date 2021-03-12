package com.github.ateranimavis.mapping;

import com.github.ateranimavis.io.IOSupplier;
import net.minecraftforge.srgutils.IRenamer;

public interface IRenamerProvider extends IOSupplier<IRenamer> {

    String describe();

    default boolean cachable() {
        return true;
    }

}
