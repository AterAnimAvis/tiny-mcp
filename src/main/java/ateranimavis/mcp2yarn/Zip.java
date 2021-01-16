package ateranimavis.mcp2yarn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import ateranimavis.mcp2yarn.io.IORunner;

public interface Zip {

    static File extract(File zip, String path, String type) throws IOException {
        return Caching.cached(Hashing.hash(zip.getName() + path), type, (file) -> extractFileFromZip(zip, path, file));
    }

    static void extractFileFromZip(File zip, String path, File destination) throws IOException {
        try (ZipFile zipFile = new ZipFile(zip)) {
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
                Files.copy(is, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
