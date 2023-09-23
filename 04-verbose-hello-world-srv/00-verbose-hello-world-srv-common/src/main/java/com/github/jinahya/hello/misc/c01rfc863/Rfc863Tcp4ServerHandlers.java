package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc863Tcp4ServerHandlers {

    // @formatter:off
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
                    log.error("failed to get addresses from " + result, ioe);
                    attachment.closeUnchecked();
                    return;
                }
                attachment.client = result;
                attachment.client.read(
                        attachment.getBufferForReading(),        // <src>
                        _Rfc86_Constants.WRITE_TIMEOUT, // <timeout>
                        _Rfc86_Constants.WRITE_TIMEOUT_UNIT,     // <unit>
                        attachment,                              // <attachment>
                        Rfc863Tcp4ServerHandlers.Read.HANDLER    // <handler>
                );
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ServerAttachment attachment) {
                log.error("failed to accept", exc);
                attachment.closeUnchecked();
            }
        }
    }
    // @formatter:on

    // @formatter:off
    enum Read implements CompletionHandler<Integer, Rfc863Tcp4ServerAttachment> {
        HANDLER() {
            @Override
            public void completed(final Integer result,
                                  final Rfc863Tcp4ServerAttachment attachment) {
                if (result == -1) {
                    attachment.closeUnchecked();
                    return;
                }
                attachment.increaseBytes(attachment.updateDigest(result));
                attachment.client.read(
                        attachment.getBufferForReading(),        // <src>
                        _Rfc86_Constants.WRITE_TIMEOUT, // <timeout>
                        _Rfc86_Constants.WRITE_TIMEOUT_UNIT,     // <unit>
                        attachment,                              // <attachment>
                        Rfc863Tcp4ServerHandlers.Read.HANDLER    // <handler>
                );
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ServerAttachment attachment) {
                log.error("failed to read", exc);
                attachment.closeUnchecked();
            }
        }
    }
    // @formatter:on

    private Rfc863Tcp4ServerHandlers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
