package com.github.jinahya.hello.misc.c02rfc862;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;

@Slf4j
final class Rfc862Tcp5ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp5ServerAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousServerSocketChannel server) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.server = Objects.requireNonNull(server, "server is null");
    }

    @Override
    public void close() throws IOException {
        group.shutdownNow();
        super.close();
    }

    // @formatter:off
    void accept() {
        server.accept(
                this,                       // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final AsynchronousSocketChannel result,
                                          final Rfc862Tcp5ServerAttachment attachment) {
                        client = result;
                        _Rfc86_Utils.logAccepted(client);
                        read();
                    }
                    @Override
                    public void failed(final Throwable exc,
                                       final Rfc862Tcp5ServerAttachment attachment) {
                        log.error("failed to accept", exc);
                        closeUnchecked();
                    }
                }
        );
    }
    // @formatter:on

    void read() {
        client.read(
                buffer,                             // <dst>
                _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                this,                               // <attachment>
                reader                              // <handler>
        );
    }

    void write() {
        buffer.flip();
        client.write(
                buffer,                              // <src>
                _Rfc86_Constants.WRITE_TIMEOUT,      // <timeout>
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                this,                                // <attachment>
                writer                               // <handler>
        );
    }

    private final AsynchronousChannelGroup group;

    private final AsynchronousServerSocketChannel server;

    private AsynchronousSocketChannel client;

    // @formatter:off
    private final CompletionHandler<Integer, Rfc862Tcp5ServerAttachment> reader =
            new CompletionHandler<>() {
                @Override
                public void completed(final Integer result,
                                      final Rfc862Tcp5ServerAttachment attachment) {
                    if (result == -1 && buffer.position() == 0) {
                        closeUnchecked();
                        return;
                    }
                    increaseBytes(result);
                    write();
                }
                @Override
                public void failed(final Throwable exc,
                                   final Rfc862Tcp5ServerAttachment attachment) {
                    log.error("failed to read", exc);
                    closeUnchecked();
                }
            };
    // @formatter:on

    // @formatter:off
    private final CompletionHandler<Integer, Rfc862Tcp5ServerAttachment> writer =
            new CompletionHandler<>() {
                @Override
                public void completed(final Integer result,
                                      final Rfc862Tcp5ServerAttachment attachment) {
                    updateDigest(result);
                    buffer.compact();
                    read();
                }
                @Override
                public void failed(final Throwable exc,
                                   final Rfc862Tcp5ServerAttachment attachment) {
                    log.error("failed to write", exc);
                    closeUnchecked();
                }
            };
    // @formatter:on
}
