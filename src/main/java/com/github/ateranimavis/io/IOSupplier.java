package com.github.ateranimavis.io;

import java.io.IOException;

public interface IOSupplier<T> {
    T get() throws IOException;
}