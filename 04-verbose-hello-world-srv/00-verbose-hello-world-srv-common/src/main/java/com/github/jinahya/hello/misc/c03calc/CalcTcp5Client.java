package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

@Slf4j
class CalcTcp5Client {

    private static void close(final AsynchronousSocketChannel channel) {
        try {
            channel.close();
        } catch (final IOException ioe) {
            log.error("failed to close", channel, ioe);
        }
    }

    private static final
    CompletionHandler<Void, AsynchronousSocketChannel> WRITTEN = new CompletionHandler<>() {
        @Override
        public void completed(final Void result, final AsynchronousSocketChannel attachment) {
        }

        @Override
        public void failed(final Throwable exc, final AsynchronousSocketChannel attachment) {
            log.debug("failed to connect");
            close(attachment);
        }
    };

    private static final
    CompletionHandler<Void, AsynchronousSocketChannel> CONNECTED = new CompletionHandler<>() {
        @Override
        public void completed(final Void result, final AsynchronousSocketChannel attachment) {
        }

        @Override
        public void failed(final Throwable exc, final AsynchronousSocketChannel attachment) {
            log.debug("failed to connect");
            close(attachment);
        }
    };

    private static void sub(final AsynchronousChannelGroup group) throws IOException {
        for (var c = 0; c < _CalcConstants.TOTAL_REQUESTS; c++) {
            final var client = AsynchronousSocketChannel.open(group);
            client.connect(
                    _CalcConstants.ADDR, // <remote>
                    client,              // <attachment>
                    CONNECTED            // <handler>
            );
        }
    }

    public static void main(final String... args) throws IOException {
        final var group = AsynchronousChannelGroup.withThreadPool(
                Executors.newFixedThreadPool(_CalcConstants.CLIENT_THREADS));
    }
}
