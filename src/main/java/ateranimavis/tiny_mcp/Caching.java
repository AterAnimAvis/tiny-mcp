package ateranimavis.tiny_mcp;

import java.io.File;
import java.io.IOException;

import ateranimavis.tiny_mcp.io.IOConsumer;

public interface Caching {

    File CACHE = new File(".cache");

    static File cached(String path, String type, IOConsumer<File> generator) throws IOException {
        CACHE.mkdirs();
        return cached(new File(CACHE, path + "." + type), generator);
    }

    static File cached(File cached, IOConsumer<File> generator) throws IOException {
        if (cached.exists()) return cached;

        generator.accept(cached);

        if (!cached.exists()) throw new AssertionError();

        return cached;
    }
}
