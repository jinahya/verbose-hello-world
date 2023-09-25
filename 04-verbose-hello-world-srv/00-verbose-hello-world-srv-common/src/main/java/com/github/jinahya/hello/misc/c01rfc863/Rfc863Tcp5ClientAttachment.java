package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;

@Slf4j
final class Rfc863Tcp5ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp5ClientAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousSocketChannel client) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // ------------------------------------------------------------------------- java.lang.Closeable

    @Override
    public void close() throws IOException {
        group.shutdownNow();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client
    void connect() {
        client.connect(
                _Rfc863Constants.ADDR,
                null,
                connected
        );
    }

    private void write() {
        client.write(
                getBufferForWriting(),
                _Rfc86_Constants.WRITE_TIMEOUT,
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT,
                null,
                written
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousChannelGroup group;

    private final AsynchronousSocketChannel client;

    // @formatter:off
    private final CompletionHandler<Void, Void> connected = new CompletionHandler<>() {
        @Override public void completed(final Void result, final Void attachment) {
            _Rfc86_Utils.logConnected(client);
            write();
        }
        @Override public void failed(final Throwable exc, final Void attachment) {
            log.error("failed to connect", exc);
            closeUnchecked();
        }
    };
    // @formatter:on

    // @formatter:off
    private final CompletionHandler<Integer, Void> written = new CompletionHandler<>() {
        @Override public void completed(final Integer result, final Void attachment) {
            if (decreaseBytes(updateDigest(result)) == 0) {
                closeUnchecked();
                return;
            }
            write();
        }
        @Override public void failed(final Throwable exc, final Void attachment) {
            log.error("failed to write", exc);
            closeUnchecked();
        }
    };
    // @formatter:on
}
