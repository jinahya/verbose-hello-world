package com.github.jinahya.hello.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class JavaUtilConcurrentCallableUtils {

    public static void callUnchecked(final Callable<?> callable,
                                     final Consumer<? super Exception> consumer) {
        Objects.requireNonNull(callable, "callable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        try {
            callable.call();
        } catch (final Exception e) {
            consumer.accept(e);
        }
    }

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
