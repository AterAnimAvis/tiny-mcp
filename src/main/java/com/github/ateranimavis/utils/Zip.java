package com.github.ateranimavis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.github.ateranimavis.io.IORunner;

public interface Zip {

    static Path extract(String description, Path zip, String path) throws IOException {
        return Caching.cached("extracted/" + description, (destination) -> extractFileFromZip(zip, path, destination));
    }

    static void extractFileFromZip(Path zip, String path, Path destination) throws IOException {
        try (ZipFile zipFile = new ZipFile(zip.toFile())) {
            InputStream is = null;

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.getName().equals(path)) {
                    is = zipFile.getInputStream(zipEntry);
                    break;
                }
            }

            if (is != null) {
                Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    static void writeFile(ZipOutputStream stream, Writer writer, String path, IORunner generator) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        stream.putNextEntry(entry);
        generator.run();
        writer.flush();
        stream.closeEntry();
    }

}
