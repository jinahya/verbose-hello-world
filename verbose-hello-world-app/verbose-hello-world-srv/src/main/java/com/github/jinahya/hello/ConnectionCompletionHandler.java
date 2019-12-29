package com.github.jinahya.hello;

import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

class ConnectionCompletionHandler<A> implements CompletionHandler<Void, A> {

    // -----------------------------------------------------------------------------------------------------------------
    ConnectionCompletionHandler(
            final BiConsumer<Void, ? super CompletionHandlerAttachment<Void, A>> completionConsumer,
            final BiConsumer<Throwable, ? super CompletionHandlerAttachment<Void, A>> failureConsumer) {
        super();
        this.completionConsumer = requireNonNull(completionConsumer, "completionConsumer is null");
        this.failureConsumer = requireNonNull(failureConsumer, "failureConsumer is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void completed(final Void result, final A attachment) {
        completionConsumer.accept(result, CompletionHandlerAttachment.of(this, attachment));
    }

    @Override
    public void failed(final Throwable exc, final A attachment) {
        failureConsumer.accept(exc, CompletionHandlerAttachment.of(this, attachment));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final BiConsumer<Void, ? super CompletionHandlerAttachment<Void, A>> completionConsumer;

    private final BiConsumer<Throwable, ? super CompletionHandlerAttachment<Void, A>> failureConsumer;
}
