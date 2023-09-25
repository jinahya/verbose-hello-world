package com.github.jinahya.hello.misc.c01rfc863;

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
final class Rfc863Tcp5ServerAttachment extends _Rfc863Attachment.Server {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param group  a channel group to be shut down when this attachment is closed.
     * @param server a server socket channel for accepting a client.
     * @see #close()
     * @see #accept()
     */
    Rfc863Tcp5ServerAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousServerSocketChannel server) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.server = Objects.requireNonNull(server, "server is null");
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    /**
     * {@inheritDoc}
     *
     * @throws IOException {@inheritDoc}
     * @apiNote Overridden to close the {@code client}, if it's not {@code null}, and shut down the
     * {@code group}.
     * @see AsynchronousSocketChannel#close()
     * @see AsynchronousChannelGroup#shutdownNow()
     */
    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
        group.shutdownNow();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client

    /**
     * Accepts a connection from {@code server}.
     *
     * @see #accepted
     */
    void accept() {
        if (client != null) {
            throw new IllegalStateException("already accepted");
        }
        server.accept(
                null,
                accepted
        );
    }

    /**
     * Reads a sequence of bytes from the {@code client}.
     *
     * @see #read
     */
    private void read() {
        if (client == null) {
            throw new IllegalStateException("client is currently null");
        }
        client.read(
                getBufferForReading(),              // <dst>
                _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                null,                               // <attachment>
                read                              // <handler>
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousChannelGroup group;

    private final AsynchronousServerSocketChannel server;

    // @formatter:off
    private final
    CompletionHandler<AsynchronousSocketChannel, Void> accepted = new CompletionHandler<>() {
        @Override
        public void completed(final AsynchronousSocketChannel result, final Void attachment) {
            client = _Rfc86_Utils.logAccepted(result);
            read();
        }
        @Override public void failed(final Throwable exc, final Void attachment) {
            log.error("failed to accept", exc, attachment);
            closeUnchecked();
        }
    };
    // @formatter:on

    private AsynchronousSocketChannel client;

    // @formatter:off
    private final CompletionHandler<Integer, Void> read = new CompletionHandler<>() {
        @Override public void completed(final Integer result, final Void attachment) {
            if (result == -1) {
                closeUnchecked();
                return;
            }
            assert result > 0; // why?
            increaseBytes(updateDigest(result));
            read();
        }
        @Override public void failed(final Throwable exc, final Void attachment) {
            log.error("failed to read", exc);
            closeUnchecked();
        }
    };
    // @formatter:on
}
