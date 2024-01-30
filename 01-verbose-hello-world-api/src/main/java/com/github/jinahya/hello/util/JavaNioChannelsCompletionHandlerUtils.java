package com.github.jinahya.hello.util;

import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class JavaNioChannelsCompletionHandlerUtils {

    // @formatter:off
    public static <V, A> CompletionHandler<V, A> from(
            final BiConsumer<? super V, ? super A> completionConsumer,
            final BiConsumer<? super Throwable, ? super A> failureConsumer) {
        Objects.requireNonNull(completionConsumer, "completionConsumer is null");
        Objects.requireNonNull(failureConsumer, "failureConsumer is null");
        return new CompletionHandler<>() {
            @Override public void completed(final V result, final A attachment) {
                completionConsumer.accept(result, attachment);
            }
            @Override public void failed(final Throwable exc, final A attachment) {
                failureConsumer.accept(exc, attachment);
            }
        };
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaNioChannelsCompletionHandlerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
