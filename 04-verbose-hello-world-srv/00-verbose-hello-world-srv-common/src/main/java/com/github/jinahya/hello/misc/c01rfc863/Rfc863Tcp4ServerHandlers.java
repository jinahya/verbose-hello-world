package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc863Tcp4ServerHandlers {

    enum Accept
            implements CompletionHandler<AsynchronousSocketChannel, Rfc863Tcp4ServerAttachment> {

        HANDLER() {
            @Override
            public void completed(final AsynchronousSocketChannel result,
                                  final Rfc863Tcp4ServerAttachment attachment) {
                try {
                    log.info("accepted from {}, through {}", result.getRemoteAddress(),
                             result.getLocalAddress());
                } catch (final IOException ioe) {
                    log.error("failed to get addresses from {}", result, ioe);
                }
                attachment.setClient(result);
                assert attachment.latch.getCount() == Rfc863Tcp4ServerAttachment.COUNT;
                attachment.latch.countDown(); // -1 for being accepted
                attachment.readWith(Read.HANDLER);
            }

            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ServerAttachment attachment) {
                log.error("failed to accept", exc);
                assert attachment.latch.getCount() == Rfc863Tcp4ServerAttachment.COUNT;
                attachment.latch.countDown();
                attachment.latch.countDown();
            }
        }
    }

    private enum Read implements CompletionHandler<Integer, Rfc863Tcp4ServerAttachment> {

        HANDLER() {
            @Override
            public void completed(final Integer result,
                                  final Rfc863Tcp4ServerAttachment attachment) {
                if (result == -1) {
                    assert attachment.latch.getCount() == Rfc863Tcp4ServerAttachment.COUNT - 1L;
                    attachment.latch.countDown();
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
                assert attachment.latch.getCount() == Rfc863Tcp4ServerAttachment.COUNT - 1L;
                attachment.latch.countDown();
            }
        }
    }

    /**
     * Creates a new instance.
     */
    private Rfc863Tcp4ServerHandlers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
