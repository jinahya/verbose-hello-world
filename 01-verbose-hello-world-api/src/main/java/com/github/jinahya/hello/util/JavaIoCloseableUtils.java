package com.github.jinahya.hello.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public final class JavaIoCloseableUtils {

    public static void closeUnchecked(final Closeable closeable,
                                      final Consumer<? super IOException> consumer) {
        Objects.requireNonNull(closeable, "closeable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        JavaUtilConcurrentCallableUtils.callUnchecked(
                () -> {
                    closeable.close();
                    return closeable;
                },
                e -> {
                    if (e instanceof IOException ioe) {
                        consumer.accept(ioe);
                        return;
                    }
                    throw new RuntimeException(e);
                }
        );
    }

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
