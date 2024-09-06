package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utilities for {@link java.io.Closeable} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
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

    public static void closeSilently(final Closeable closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.error("failed to close {}", closeable, ioe);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaIoCloseableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
