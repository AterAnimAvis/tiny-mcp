package com.github.ateranimavis.mapping.base;

import java.io.IOException;
import java.nio.file.Path;

import com.github.ateranimavis.mapping.IMappingProvider;
import com.github.ateranimavis.utils.Lazy;
import net.minecraftforge.srgutils.IMappingFile;
import net.minecraftforge.srgutils.INamedMappingFile;

public class BaseProvider implements IMappingProvider {

    private final String description;
    private final Lazy<IMappingFile> mapping;

    public BaseProvider(String description, Lazy<IMappingFile> mapping) {
        this.description = description;
        this.mapping = mapping;
    }

    @Override
    public String describe() {
        return description;
    }

    @Override
    public IMappingFile get() throws IOException {
        return mapping.get();
    }

    public static IMappingFile getMappings(Path path) throws IOException {
        return IMappingFile.load(path.toFile());
    }

    public static INamedMappingFile getNamedMappings(Path path) throws IOException {
        return INamedMappingFile.load(path.toFile());
    }

}
