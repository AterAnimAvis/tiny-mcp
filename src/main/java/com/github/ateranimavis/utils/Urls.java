package com.github.ateranimavis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public interface Urls {

    static Path download(String cached, String url) throws IOException {
        return Caching.cached(cached, (destination) -> downloadFileFromUrl(url, destination));
    }

    static Path uncachedDownload(String cached, String url) throws IOException {
        return Caching.uncached(cached, (path) -> downloadFileFromUrl(url, path));
    }

    static void downloadFileFromUrl(String url, Path destination) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
