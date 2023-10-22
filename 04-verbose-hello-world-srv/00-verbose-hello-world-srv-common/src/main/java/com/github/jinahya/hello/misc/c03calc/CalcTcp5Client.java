package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

@Slf4j
class CalcTcp5Client {

    private static final
    CompletionHandler<Integer, CalcTcp5Attachment> READ = new CompletionHandler<>() {
        @Override // @formatter:off
        public void completed(final Integer result, final CalcTcp5Attachment attachment) {
            if (attachment.hasRemaining()) {
                attachment.read(this);
                return;
            }
            attachment.log().closeUnchecked();
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
            if (attachment.hasRemaining()) {
                attachment.write(this);
                return;
            }
            attachment.readyToReceiveResult().read(READ);
        }
        @Override
        public void failed(final Throwable exc, final CalcTcp5Attachment attachment) {
            log.debug("failed to write", exc);
            attachment.closeUnchecked();
        } // @formatter:on
    };

    private static final
    CompletionHandler<Void, CalcTcp5Attachment> CONNECTED = new CompletionHandler<>() {
        @Override // @formatter:off
        public void completed(final Void result, final CalcTcp5Attachment attachment) {
            attachment.write(WRITTEN);
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
            CalcTcp5Attachment
                    .newInstanceForClient(AsynchronousSocketChannel.open(group), latch)
                    .connect(CONNECTED);
        }
        try {
            final var terminated = latch.await(_CalcConstants.CLIENT_PROGRAM_TIMEOUT,
                                               _CalcConstants.CLIENT_PROGRAM_TIMEOUT_UNIT);
            assert terminated : "latch hasn't been broken";
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting the latch", ie);
            Thread.currentThread().interrupt();
        }
        group.shutdown();
    }

    public static void main(final String... args) throws IOException {
        sub(_CalcUtils.newChannelGroupForServer());
    }
}
