package com.github.jinahya.hello.util;

import java.io.Closeable;
import java.util.Objects;

/**
 * Utilities for {@link java.io.Closeable} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaIoCloseableUtils {

    public static <T extends Closeable> T closeUnchecked(final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            closeable.close();
            return closeable;
        });
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaIoCloseableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
