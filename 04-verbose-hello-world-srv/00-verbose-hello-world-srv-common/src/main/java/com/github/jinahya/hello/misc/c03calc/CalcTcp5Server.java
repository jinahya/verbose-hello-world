package com.github.jinahya.hello.misc.c03calc;

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
class CalcTcp5Server {

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> WRITTEN = new CompletionHandler<>() {
        @Override // @formatter:off
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            if (attachment.hasRemaining()) {
                attachment.write(this);
                return;
            }
            attachment.closeUnchecked();
        }
        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to write", exc);
            attachment.closeUnchecked();
        } // @formatter:on
    };

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> READ = new CompletionHandler<>() {
        @Override // @formatter:off
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            if (attachment.hasRemaining()) {
                attachment.read(this);
                return;
            }
            attachment.apply().readyToSendResult().write(WRITTEN);
        }
        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to read", exc);
            attachment.closeUnchecked();
        } // @formatter:on
    };

    @SuppressWarnings({
            "java:S2095" // use try-with-resources fo CalcTcp5Attachment
    })
    private static void sub(final AsynchronousServerSocketChannel server) {
        server.<Void>accept(
                null,                       // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override // @formatter:off
                    public void completed(final AsynchronousSocketChannel result,
                                          final Void attachment) {
                        CalcTcp5Attachment.newInstanceForServer(result).read(READ);
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
                    } // @formatter:on
                }
        );
    }

    public static void main(final String... args) throws IOException {
        final var group = _CalcUtils.newChannelGroupForServers();
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            // ------------------------------------------------------------------------------- reuse
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            server.bind(_CalcConstants.ADDR, _CalcConstants.SERVER_BACKLOG);
            _TcpUtils.logBound(server);
            // --------------------------------------------------------------------------------- sub
            sub(server);
            // ------------------------------------------------------- read-quite-and-shutdown-group
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit,
                    () -> {
                        group.shutdownNow();
                        return null;
                    }
            );
            // ------------------------------------------------------------------------- await-group
            while (!group.isTerminated()) {
                try {
                    final var terminated = group.awaitTermination(
                            _CalcConstants.SERVER_PROGRAM_TIMEOUT,
                            _CalcConstants.SERVER_PROGRAM_TIMEOUT_UNIT
                    );
                    if (terminated) {
                        log.debug("channel group is terminated");
                    }
                } catch (final InterruptedException ie) {
                    log.error("interrupted while awaiting channel group terminated", ie);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
