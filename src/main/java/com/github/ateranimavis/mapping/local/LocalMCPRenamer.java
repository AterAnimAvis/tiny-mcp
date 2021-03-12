package com.github.ateranimavis.mapping.local;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.ateranimavis.mapping.base.BaseMCPRenamer;
import com.github.ateranimavis.utils.Lazy;

public class LocalMCPRenamer extends BaseMCPRenamer {

    public LocalMCPRenamer(String fileName) {
        this(fileName, Paths.get(fileName));
    }

    public LocalMCPRenamer(String description, Path mappings) {
        super(description, Lazy.of(() -> getRenamer(description, mappings)));
    }

    @Override
    public boolean cachable() {
        return false;
    }
}
