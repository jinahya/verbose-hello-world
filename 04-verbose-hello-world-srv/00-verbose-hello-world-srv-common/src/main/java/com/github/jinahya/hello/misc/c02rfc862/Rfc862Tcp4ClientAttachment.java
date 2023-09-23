package com.github.jinahya.hello.misc.c02rfc862;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.util.JavaLangReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp4ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp4ClientAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousSocketChannel client) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.client = Objects.requireNonNull(client, "client is null");
        proxy = JavaLangReflectUtils.uncloseableProxy(this.client);
    }

    @Override
    public void close() throws IOException {
        group.shutdownNow();
        super.close();
    }

    // -------------------------------------------------------------------------------------- buffer

    ByteBuffer bufferForWriting() {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.limit(Math.min(buffer.remaining(), getBytes()));
        }
        return buffer;
    }

    ByteBuffer bufferForReading() {
        return buffer.flip();
    }

    // --------------------------------------------------------------------------------------- group

    // -------------------------------------------------------------------------------------- client

    AsynchronousSocketChannel client() {
        return proxy;
    }

    void shutdownClientOutput() throws IOException {
        client.shutdownOutput();
    }

    void shutdownClientOutputUnchecked() {
        try {
            shutdownClientOutput();
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to shutdown output of " + client, ioe);
        }
    }

    void connect() {
        client.connect(
                _Rfc862Constants.ADDR,
                this,
                new CompletionHandler<>() { // @formatter:off
                    @Override
                    public void completed(final Void result,
                                          final Rfc862Tcp4ClientAttachment attachment) {
                        try {
                            log.info("connected to {}, through {}", client.getRemoteAddress(),
                                     client.getLocalAddress());
                        } catch (final IOException ioe) {
                            throw new UncheckedIOException(ioe);
                        }
                        Rfc862Tcp4ClientHandlers.Connect.HANDLER.completed(result, attachment);
                    }
                    @Override
                    public void failed(final Throwable exc,
                                       final Rfc862Tcp4ClientAttachment attachment) {
                        Rfc862Tcp4ClientHandlers.Connect.HANDLER.failed(exc, attachment);
                    }
                } // @formatter:on
        );
    }

    void write() {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.limit(Math.min(buffer.remaining(), getBytes()));
        }
        client.write(
                buffer,
                _Rfc86_Constants.READ_TIMEOUT,
                _Rfc86_Constants.READ_TIMEOUT_UNIT,
                this,
                new CompletionHandler<>() { // @formatter:off
                    @Override
                    public void completed(Integer result, Rfc862Tcp4ClientAttachment attachment) {
                        updateDigest(result);
                        if (decreaseBytes(result) == 0) {
                            try {
                                client.shutdownOutput();
                            } catch (final IOException ioe) {
                                log.error("failed to shutdown output", ioe);
                                closeUnchecked();
                                return;
                            }
                            logDigest();
                        }
                        Rfc862Tcp4ClientHandlers.Write.HANDLER.completed(result, attachment);
                    }
                    @Override
                    public void failed(Throwable exc, Rfc862Tcp4ClientAttachment attachment) {
                        Rfc862Tcp4ClientHandlers.Write.HANDLER.failed(exc, attachment);
                    }
                } // @formatter:on
        );
    }

    void read() {
        client.read(
                buffer.flip(),
                _Rfc86_Constants.READ_TIMEOUT,
                _Rfc86_Constants.READ_TIMEOUT_UNIT,
                this,
                new CompletionHandler<>() { // @formatter:off
                    @Override
                    public void completed(final Integer result,
                                          final Rfc862Tcp4ClientAttachment attachment) {
                        buffer.position(buffer.limit()).limit(buffer.capacity());
                        Rfc862Tcp4ClientHandlers.Read.HANDLER.completed(result, attachment);
                    }
                    @Override
                    public void failed(final Throwable exc,
                                       final Rfc862Tcp4ClientAttachment attachment) {
                        Rfc862Tcp4ClientHandlers.Read.HANDLER.failed(exc, attachment);
                    }
                } // @formatter:on
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousChannelGroup group;

    private final AsynchronousSocketChannel client;

    private final AsynchronousSocketChannel proxy;
}
