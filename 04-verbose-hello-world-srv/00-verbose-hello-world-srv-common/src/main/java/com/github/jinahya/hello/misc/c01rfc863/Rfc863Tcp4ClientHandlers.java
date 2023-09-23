package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc863Tcp4ClientHandlers {

    // @formatter:off
    enum Connect implements CompletionHandler<Void, Rfc863Tcp4ClientAttachment> {
        HANDLER() {
            @Override
            public void completed(final Void result, final Rfc863Tcp4ClientAttachment attachment) {
                try {
                    log.info("connected to {}, through {}", attachment.client.getRemoteAddress(),
                             attachment.client.getLocalAddress());
                } catch (final IOException ioe) {
                    log.error("failed to get addresses from " + attachment.client, ioe);
                }
                attachment.client.write(
                        attachment.getBufferForWriting(),
                        _Rfc86_Constants.WRITE_TIMEOUT,
                        _Rfc86_Constants.WRITE_TIMEOUT_UNIT,
                        attachment,
                        Write.HANDLER
                );
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ClientAttachment attachment) {
                log.error("failed to connect", exc);
                attachment.closeUnchecked();
            }
        }
    }
    // @formatter:on

    // @formatter:off
    enum Write implements CompletionHandler<Integer, Rfc863Tcp4ClientAttachment> {
        HANDLER() {
            @Override
            public void completed(final Integer result,
                                  final Rfc863Tcp4ClientAttachment attachment) {
                if (attachment.decreaseBytes(attachment.updateDigest(result)) == 0) {
                    attachment.closeUnchecked();
                    return;
                }
                attachment.client.write(
                        attachment.getBufferForWriting(),        // <src>
                        _Rfc86_Constants.WRITE_TIMEOUT, // <timeout>
                        _Rfc86_Constants.WRITE_TIMEOUT_UNIT,     // <unit>
                        attachment,                              // <attachment>
                        this                                     // <handler>
                );
            }
            @Override
            public void failed(final Throwable exc, final Rfc863Tcp4ClientAttachment attachment) {
                log.error("failed to write", exc);
                attachment.closeUnchecked();
            }
        }
    }
    // @formatter:on

    private Rfc863Tcp4ClientHandlers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
