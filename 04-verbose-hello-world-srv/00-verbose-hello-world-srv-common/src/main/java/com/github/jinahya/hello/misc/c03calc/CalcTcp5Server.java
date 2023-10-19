package com.github.jinahya.hello.misc.c03calc;

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

@Slf4j
class CalcTcp5Server {

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> WRITTEN = new CompletionHandler<>() {
        @Override
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            log.debug("written: {}", result);
            if (attachment.buffer.hasRemaining()) {
                attachment.write(this);
                return;
            }
            attachment.closeUnchecked();
        }

        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to write", exc);
            attachment.closeUnchecked();
        }
    };

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> READ = new CompletionHandler<>() {
        @Override
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            log.debug("read: {}", result);
            if (attachment.buffer.hasRemaining()) {
                attachment.read(this);
                return;
            }
            _CalcMessage.apply(attachment.buffer);
            assert attachment.buffer.remaining() == _CalcMessage.LENGTH_RESPONSE;
            attachment.write(WRITTEN);
        }

        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to read", exc);
            attachment.closeUnchecked();
        }
    };

    @SuppressWarnings({
            "java:S2095" // use try-with-resources fo CalcTcp5Attachment
    })
    private static void sub(final AsynchronousServerSocketChannel server) {
        server.<Void>accept(
                null,                       // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final AsynchronousSocketChannel result,
                                          final Void attachment) {
                        log.debug("accepted: {}", result);
                        final var attachment_ = CalcTcp5Attachment.newInstanceForServer(result);
                        assert attachment_.buffer.position() == 0;
                        assert attachment_.buffer.remaining() == _CalcMessage.LENGTH_REQUEST;
                        attachment_.read(READ);
                        server.accept(
                                null, // <attachment>
                                this  // <handler>
                        );
                    }

                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        if (server.isOpen()) {
                            log.error("failed to accept", exc);
                        }
                    }
                }
        );
    }

    public static void main(final String... args) throws IOException {
        final var group = AsynchronousChannelGroup.withThreadPool(
                Executors.newFixedThreadPool(_CalcConstants.CLIENT_THREADS));
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_CalcConstants.ADDR, _CalcConstants.SERVER_BACKLOG);
            _TcpUtils.logBound(server);
            // ----------------------------------------- read "quit!" and shutdown the channel group
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit,
                    () -> {
                        log.debug("shutting down {}", group);
                        group.shutdownNow();
                        return null;
                    }
            );
            // --------------------------------------------------------------------------------- sub
            sub(server);
            // ----------------------------------------------------------------- await channel group
            try {
                final var terminated = group.awaitTermination(
                        _CalcConstants.SERVER_PROGRAM_TIMEOUT,
                        _CalcConstants.SERVER_PROGRAM_TIMEOUT_UNIT
                );
                assert terminated;
            } catch (final InterruptedException ie) {
                log.error("interrupted while awaiting group to be terminated", ie);
                Thread.currentThread().interrupt();
            }
        }
    }
}
