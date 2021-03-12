package com.github.ateranimavis.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.ateranimavis.io.IOConsumer;

public interface Caching {

    Path CACHE = Paths.get(".cache");

    static Path cached(String path, IOConsumer<Path> generator) throws IOException {
        return cached(CACHE.resolve(path), generator);
    }

    static Path cached(Path cached, IOConsumer<Path> generator) throws IOException {
        if (Files.exists(cached)) return cached;

        Files.createDirectories(cached.getParent());

        System.out.printf("Caching: no cached copy of %s, running generator%n", cached);
        generator.accept(cached);

        if (!Files.exists(cached)) throw new AssertionError("Generator did not generate the requested file");

        return cached;
    }
}
