package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc863Tcp4ClientHandlers {

    enum Connect implements CompletionHandler<Void, Rfc863Tcp4ClientAttachment> {

        HANDLER() {
            @Override
            public void completed(final Void result, final Rfc863Tcp4ClientAttachment attachment) {
                try {
                    log.info("connected to {}, through {}", attachment.client.getRemoteAddress(),
                             attachment.client.getLocalAddress());
                } catch (final IOException ioe) {
                    log.error("failed to get addresses from {}", attachment.client, ioe);
                }
                assert attachment.latch.getCount() == 2;
                attachment.latch.countDown(); // -1 for being connected
                attachment.writeWith(Write.HANDLER);
            }

            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ClientAttachment attachment) {
                log.error("failed to connect", exc);
                attachment.closeUnchecked();
            }
        }
    }

    private enum Write
            implements CompletionHandler<Integer, Rfc863Tcp4ClientAttachment> {

        HANDLER() {
            @Override
            public void completed(final Integer result,
                                  final Rfc863Tcp4ClientAttachment attachment) {
                attachment.updateDigest(result);
                if (attachment.decreaseBytes(result) == 0) {
//                    attachment.logDigest();
                    assert attachment.latch.getCount() == 1;
                    attachment.latch.countDown();
                    return;
                }
                attachment.writeWith(this);
            }

            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ClientAttachment attachment) {
                log.error("failed to write", exc);
                assert attachment.latch.getCount() == 1;
                attachment.latch.countDown();
            }
        }
    }

    private Rfc863Tcp4ClientHandlers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
