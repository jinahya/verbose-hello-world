package com.github.jinahya.hello.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.Callable;

public class JavaUtilConcurrentCallableUtils {

    public static <V> V callUnchecked(final Callable<V> callable) {
        Objects.requireNonNull(callable, "callable is null");
        try {
            return callable.call();
        } catch (final Exception e) {
            if (e instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            throw new RuntimeException("failed to call " + callable, e);
        }
    }

    private JavaUtilConcurrentCallableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
