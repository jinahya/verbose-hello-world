package com.github.jinahya.hello.util;

import java.io.Closeable;
import java.util.Objects;

public final class JavaIoCloseableUtils {

    public static <T extends Closeable> T closeUnchecked(final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            closeable.close();
            return closeable;
        });
    }

    private JavaIoCloseableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
