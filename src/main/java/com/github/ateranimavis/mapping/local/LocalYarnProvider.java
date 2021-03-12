package com.github.ateranimavis.mapping.local;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.ateranimavis.mapping.base.BaseProvider;
import com.github.ateranimavis.utils.Lazy;

public class LocalYarnProvider extends BaseProvider {

    public LocalYarnProvider(String fileName) {
        this(fileName, Paths.get(fileName));
    }

    public LocalYarnProvider(String description, Path path) {
        super(description, Lazy.of(() -> getNamedMappings(path).getMap("official", "intermediary")));
    }

    @Override
    public boolean cachable() {
        return false;
    }

}
