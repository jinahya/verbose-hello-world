package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@Slf4j
class CalcTcp5Client {

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> READ = new CompletionHandler<>() {
        @Override // @formatter:off
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            if (attachment.buffer.hasRemaining()) {
                attachment.read(this);
                return;
            }
            _CalcMessage.log(attachment.buffer);
            attachment.closeUnchecked();
        }
        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to read", exc);
            attachment.closeUnchecked();
        } // @formatter:on
    };

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> WRITTEN = new CompletionHandler<>() {
        @Override // @formatter:off
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            if (attachment.buffer.hasRemaining()) {
                attachment.write(this);
                return;
            }
            assert attachment.buffer.position() == _CalcMessage.LENGTH_REQUEST;
            attachment.buffer.limit(attachment.buffer.capacity());
            attachment.read(READ);
        }
        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to write", exc);
            attachment.closeUnchecked();
        } // @formatter:on
    };

    @SuppressWarnings({
            "java:S2095" // use try-with-resources fo CalcTcp5Attachment
    })
    private static void sub(final AsynchronousChannelGroup group) throws IOException {
        final var latch = new CountDownLatch(_CalcConstants.TOTAL_REQUESTS);
        for (var c = 0; c < _CalcConstants.TOTAL_REQUESTS; c++) {
            final var client = AsynchronousSocketChannel.open(group);
            client.connect(
                    _CalcConstants.ADDR,        // <remote>
                    client,                     // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override // @formatter:off
                        public void completed(final Void result,
                                              final AsynchronousSocketChannel attachment) {
                            final var attachment_ =
                                    CalcTcp5Attachment.newInstanceForClient(attachment, latch);
                            attachment_.write(WRITTEN);
                        }
                        @Override
                        public void failed(final Throwable exc,
                                           final AsynchronousSocketChannel attachment) {
                            log.error("failed to connect", exc);
                            latch.countDown();
                        } // @formatter:on
                    }
            );
        }
        try {
            final var terminated = latch.await(_CalcConstants.CLIENT_PROGRAM_TIMEOUT,
                                               _CalcConstants.CLIENT_PROGRAM_TIMEOUT_UNIT);
            assert terminated;
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting the latch", ie);
            Thread.currentThread().interrupt();
        }
        group.shutdown();
    }

    public static void main(final String... args) throws IOException {
        final var group = AsynchronousChannelGroup.withThreadPool(
                Executors.newFixedThreadPool(_CalcConstants.CLIENT_THREADS));
        sub(group);
    }
}
