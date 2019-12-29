package com.github.jinahya.hello;

import lombok.Getter;
import lombok.Setter;

import java.nio.channels.CompletionHandler;

@Setter
@Getter
class CompletionHandlerAttachment<V, A> {

    // -----------------------------------------------------------------------------------------------------------------
    static <V, A> CompletionHandlerAttachment<V, A> of(final CompletionHandler<V, ? super A> handler,
                                                       final A attachment) {
        final CompletionHandlerAttachment<V, A> instance = new CompletionHandlerAttachment<>();
        instance.handler = handler;
        instance.attachment = attachment;
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private CompletionHandlerAttachment() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private CompletionHandler<V, ? super A> handler;

    private A attachment;
}
