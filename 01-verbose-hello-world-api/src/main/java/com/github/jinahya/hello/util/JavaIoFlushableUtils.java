package com.github.jinahya.hello.util;

import java.io.Flushable;
import java.util.Objects;

/**
 * Utilities for {@link java.io.Flushable} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaIoFlushableUtils {

    public static <T extends Flushable> T flushUnchecked(final T flushable) {
        Objects.requireNonNull(flushable, "flushable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            flushable.flush();
            return flushable;
        });
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaIoFlushableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
