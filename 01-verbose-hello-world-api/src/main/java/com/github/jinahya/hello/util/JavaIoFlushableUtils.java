package com.github.jinahya.hello.util;

import java.io.Flushable;
import java.util.Objects;

public final class JavaIoFlushableUtils {

    public static <T extends Flushable> T flushUnchecked(final T flushable) {
        Objects.requireNonNull(flushable, "flushable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            flushable.flush();
            return flushable;
        });
    }

    private JavaIoFlushableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
