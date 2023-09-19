package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc863Tcp4ClientHandlers {

    enum Connect implements CompletionHandler<Void, Rfc863Tcp4ClientAttachment> {

        HANDLER() { // @formatter:off
            @Override
            public void completed(final Void result, final Rfc863Tcp4ClientAttachment attachment) {
                attachment.logConnected();
                attachment.countDownLatch(Rfc863Tcp4ClientAttachment.LATCH_COUNT);
                attachment.write(Write.HANDLER);
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ClientAttachment attachment) {
                log.error("failed to connect", exc);
                attachment.closeUnchecked();
                attachment.countDownLatch(Rfc863Tcp4ClientAttachment.LATCH_COUNT);
                attachment.countDownLatch(Rfc863Tcp4ClientAttachment.LATCH_COUNT - 1L);
            }
        } // @formatter:on
    }

    private enum Write
            implements CompletionHandler<Integer, Rfc863Tcp4ClientAttachment> {

        HANDLER() { // @formatter:off
            @Override
            public void completed(final Integer result,
                                  final Rfc863Tcp4ClientAttachment attachment) {
                attachment.updateDigest(result);
                if (attachment.decreaseBytes(result) == 0) {
                    attachment.countDownLatch(Rfc863Tcp4ClientAttachment.LATCH_COUNT - 1L);
                    return;
                }
                attachment.write(this);
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ClientAttachment attachment) {
                log.error("failed to write", exc);
                attachment.closeUnchecked();
                attachment.countDownLatch(Rfc863Tcp4ClientAttachment.LATCH_COUNT - 1L);
            }
        } // @formatter:on
    }

    /**
     * Creates a new instance.
     */
    private Rfc863Tcp4ClientHandlers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
