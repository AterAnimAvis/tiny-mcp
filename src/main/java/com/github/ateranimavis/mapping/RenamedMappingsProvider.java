package com.github.ateranimavis.mapping;

import java.io.IOException;
import java.nio.file.Path;

import com.github.ateranimavis.utils.Caching;
import net.minecraftforge.srgutils.IMappingFile;

public class RenamedMappingsProvider implements IMappingProvider {

    private final IMappingProvider mapping;
    private final IRenamerProvider renamer;

    public RenamedMappingsProvider(IMappingProvider mapping, IRenamerProvider renamer) {
        this.mapping = mapping;
        this.renamer = renamer;
    }

    @Override
    public String describe() {
        return mapping.describe() + " " + renamer.describe();
    }

    @Override
    public IMappingFile get() throws IOException {
        return getRenamed(mapping, renamer);
    }

    public static IMappingFile getRenamed(IMappingProvider mapping, IRenamerProvider renamer) throws IOException {
        if (mapping.cachable() && renamer.cachable()) {
            return IMappingFile.load(Caching.cached("combined/" + mapping.describe().replace("/", "-") + "-" + renamer.describe().replace("/", "-") + ".tsrg", (path) -> generateRenamed(mapping, renamer, path)).toFile());
        }

        return generateRenamed(mapping, renamer);
    }

    static void generateRenamed(IMappingProvider mapping, IRenamerProvider renamer, Path file) throws IOException {
        IMappingFile mappings = generateRenamed(mapping, renamer);
        mappings.write(file, IMappingFile.Format.TSRG, false);
    }

    static IMappingFile generateRenamed(IMappingProvider mapping, IRenamerProvider renamer) throws IOException {
        return mapping.get().rename(renamer.get());
    }

}
