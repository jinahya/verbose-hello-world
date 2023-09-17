package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc863Tcp4ServerHandlers {

    enum Accept
            implements CompletionHandler<AsynchronousSocketChannel, Rfc863Tcp4ServerAttachment> {

        HANDLER() { // @formatter:off
            @Override
            public void completed(final AsynchronousSocketChannel result,
                                  final Rfc863Tcp4ServerAttachment attachment) {
                attachment.setClient(result);
                attachment.countDownLatch(Rfc863Tcp4ServerAttachment.LATCH_COUNT); // accepted
                attachment.readWith(Read.HANDLER);
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ServerAttachment attachment) {
                log.error("failed to accept", exc);
                attachment.closeUnchecked();
                attachment.countDownLatch(Rfc863Tcp4ServerAttachment.LATCH_COUNT);
                attachment.countDownLatch(Rfc863Tcp4ServerAttachment.LATCH_COUNT - 1L);
            }
        } // @formatter:on
    }

    private enum Read implements CompletionHandler<Integer, Rfc863Tcp4ServerAttachment> {

        HANDLER() { // @formatter:off
            @Override
            public void completed(final Integer result,
                                  final Rfc863Tcp4ServerAttachment attachment) {
                if (result == -1) {
                    attachment.closeUnchecked();
                    attachment.countDownLatch(Rfc863Tcp4ServerAttachment.LATCH_COUNT - 1L);
                    return;
                }
                assert result > 0; // why?
                attachment.updateDigest(result);
                attachment.increaseBytes(result);
                attachment.readWith(this);
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ServerAttachment attachment) {
                log.error("failed to read", exc);
                attachment.closeUnchecked();
                attachment.countDownLatch(Rfc863Tcp4ServerAttachment.LATCH_COUNT - 1L);
            }
        } // @formatter:on
    }

    /**
     * Creates a new instance.
     */
    private Rfc863Tcp4ServerHandlers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
