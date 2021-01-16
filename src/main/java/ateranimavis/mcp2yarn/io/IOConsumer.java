package ateranimavis.mcp2yarn.io;

import java.io.IOException;

public interface IOConsumer<T> {
    void accept(T file) throws IOException;
}
