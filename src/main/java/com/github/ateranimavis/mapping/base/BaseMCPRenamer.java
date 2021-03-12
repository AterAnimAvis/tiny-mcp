package com.github.ateranimavis.mapping.base;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.github.ateranimavis.mapping.IRenamerProvider;
import com.github.ateranimavis.utils.CSV;
import com.github.ateranimavis.utils.Lazy;
import com.github.ateranimavis.utils.Scanners;
import net.minecraftforge.srgutils.IMappingFile;
import net.minecraftforge.srgutils.IRenamer;

public class BaseMCPRenamer implements IRenamerProvider {

    private final String description;
    private final Lazy<IRenamer> mapping;

    public BaseMCPRenamer(String description, Lazy<IRenamer> mapping) {
        this.description = description;
        this.mapping = mapping;
    }

    @Override
    public String describe() {
        return description;
    }

    @Override
    public IRenamer get() throws IOException {
        return mapping.get();
    }

    public static IRenamer getRenamer(String description, Path mappings) throws IOException {
        Map<String, String> methods = CSV.readPairs(Scanners.mappingsMethods(description, mappings));
        Map<String, String> fields = CSV.readPairs(Scanners.mappingsFields(description, mappings));

        return new IRenamer() {
            @Override
            public String rename(IMappingFile.IField value) {
                return fields.getOrDefault(value.getMapped(), value.getMapped());
            }

            @Override
            public String rename(IMappingFile.IMethod value) {
                return methods.getOrDefault(value.getMapped(), value.getMapped());
            }
        };
    }

}
