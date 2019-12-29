package com.github.jinahya.hello;

import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

class ReadingCompletionHandler<A> implements CompletionHandler<Integer, A> {

    // -----------------------------------------------------------------------------------------------------------------
    ReadingCompletionHandler(
            final BiConsumer<Integer, ? super CompletionHandlerAttachment<Integer, A>> completionConsumer,
            final BiConsumer<Throwable, ? super CompletionHandlerAttachment<Integer, A>> failureConsumer) {
        super();
        this.completionConsumer = requireNonNull(completionConsumer, "completionConsumer is null");
        this.failureConsumer = requireNonNull(failureConsumer, "failureConsumer is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void completed(final Integer result, final A attachment) {
        completionConsumer.accept(result, CompletionHandlerAttachment.of(this, attachment));
    }

    @Override
    public void failed(final Throwable exc, final A attachment) {
        failureConsumer.accept(exc, CompletionHandlerAttachment.of(this, attachment));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final BiConsumer<Integer, ? super CompletionHandlerAttachment<Integer, A>> completionConsumer;

    private final BiConsumer<Throwable, ? super CompletionHandlerAttachment<Integer, A>> failureConsumer;
}
