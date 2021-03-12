package com.github.ateranimavis.utils;

import java.io.IOException;

import com.github.ateranimavis.io.IOSupplier;

public interface Lazy<T> extends IOSupplier<T> {

    static <T> Lazy<T> of(IOSupplier<T> supplier) {
        return new Impl<>(supplier);
    }

    final class Impl<T> implements Lazy<T> {

        private IOSupplier<T> supplier;
        private T instance;

        private Impl(IOSupplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public final T get() throws IOException {
            if (supplier != null)
                resolve();

            return instance;
        }

        private void resolve() throws IOException {
            instance = supplier.get();
            supplier = null;
        }

    }

}
