package com.github.ateranimavis.mapping.local;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.ateranimavis.mapping.base.BaseProvider;
import com.github.ateranimavis.utils.Lazy;

public class LocalProvider extends BaseProvider {

    public LocalProvider(String fileName) {
        this(fileName, Paths.get(fileName));
    }

    public LocalProvider(String description, Path path) {
        super(description, Lazy.of(() -> getMappings(path)));
    }

    @Override
    public boolean cachable() {
        return false;
    }
}
