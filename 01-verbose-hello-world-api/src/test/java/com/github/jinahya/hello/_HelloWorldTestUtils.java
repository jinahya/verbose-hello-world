package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class _HelloWorldTestUtils {

    private static <T> T requireMock(final T object) {
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("not a mock: " + object);
        }
        return object;
    }

    private static <T> T requireSpy(final T object) {
        if (!Mockito.mockingDetails(object).isSpy()) {
            throw new IllegalArgumentException("not a spy: " + object);
        }
        return object;
    }

    // ------------------------------------------------------------------------- WritableByteChannel

    /**
     * Stubs specified channel's {@link WritableByteChannel#write(ByteBuffer) write(src)} method to
     * increases the {@code src}'s position by a random value.
     *
     * @param channel the channel whose {@link WritableByteChannel#write(ByteBuffer) write(src)}
     *                method is stubbed.
     * @param adder   an adder for accumulating the number of bytes written; may be {@code null}.
     * @throws IOException if an I/O error occurs.
     */
    public static void write_willWriteSome(final WritableByteChannel channel,
                                           final LongAdder adder)
            throws IOException {
        requireMock(Objects.requireNonNull(channel, "channel is null"));
        given(channel.write(notNull())).willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            if (adder != null) {
                adder.add(written);
            }
            return written;
        });
    }

    // --------------------------------------------------------------------- AsynchronousByteChannel

    /**
     * Stubs specified channel's {@link WritableByteChannel#write(ByteBuffer) write(src)} method to
     * return a future increases the {@code src}'s position by a random value.
     *
     * @param channel the channel whose {@link WritableByteChannel#write(ByteBuffer)} method are
     *                stubbed.
     * @param adder   an adder for accumulating the number of written bytes; may be {@code null}.
     */
    public static void write_willReturnFutureDrainsBuffer(final AsynchronousByteChannel channel,
                                                          final LongAdder adder) {
        requireMock(channel);
        final var futureReference = new AtomicReference<Future<Integer>>();
        given(channel.write(argThat(b -> b != null && b.hasRemaining()))).willAnswer(w -> {
            final var previousFuture = futureReference.get();
            if (previousFuture != null) {
                verify(previousFuture, Mockito.times(1)).get();
            }
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            given(future.get()).willAnswer(g -> {
                final var written = current().nextInt(1, src.remaining() + 1);
                src.position(src.position() + written);
                if (adder != null) {
                    adder.add(written);
                }
                return written;
            });
            futureReference.set(future);
            return future;
        });
    }

    public static void write_willReturnFutureFails(final AsynchronousByteChannel channel) {
        requireMock(channel);
        given(channel.write(argThat(b -> b != null && b.hasRemaining()))).willAnswer(w -> {
            final var future = Mockito.mock(Future.class);
            given(future.get()).willThrow(new ExecutionException(new RuntimeException()));
            return future;
        });
    }

    @SuppressWarnings({
            "unchecked"
    })
    public static void write_invokeHandlerCompleted(final AsynchronousByteChannel channel,
                                                    final LongAdder adder) {
        requireMock(channel);
    }

    @SuppressWarnings({"unchecked"})
    public static void writeWithHandler_fails(final AsynchronousByteChannel given) {
        requireMock(Objects.requireNonNull(given, "given is null"));
        willAnswer(i -> {
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            handler.failed(new Exception(), attachment);
            return null;
        }).given(given).write(
                argThat(b -> b != null && b.hasRemaining()),
                any(),
                notNull()
        );
    }

    // --------------------------------------------------------------------- AsynchronousFileChannel

    /**
     * Stubs specified channel's
     * {@link AsynchronousFileChannel#write(ByteBuffer, long) write(src, position)} method to return
     * a future writes some bytes from {@code src} starting at specified position while adding the
     * number of written bytes to specified adder.
     *
     * @param channel the channel whose
     *                {@link AsynchronousFileChannel#write(ByteBuffer, long) write(src, position)}
     *                method is stubbed.
     * @param adder   the adder to which the number of written bytes is added.
     */
    public static void writeBuffer_willWriteSome(final AsynchronousFileChannel channel,
                                                 final LongAdder adder) {
        requireMock(channel);
    }

    /**
     * Stubs specified channel's
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * write(channel, position, attachment, handler)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)}.
     *
     * @param channel the channel to be stubbed.
     * @param exc     the error for the {@code exc} parameter.
     */
    @SuppressWarnings({"unchecked"})
    protected static <T extends Throwable> T _stub_ToFail(AsynchronousFileChannel channel,
                                                          final T exc) {
        requireMock(channel);
        Objects.requireNonNull(exc, "exc is null");
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var handler = i.getArgument(3, CompletionHandler.class);
            handler.failed(exc, attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()),
                longThat(v -> v >= 0L),
                any(),
                notNull()
        );
        return exc;
    }

    @SuppressWarnings({"unchecked"})
    protected static void _stub_ToComplete(final AsynchronousFileChannel channel,
                                           final LongAdder adder) {
        requireMock(channel);
        willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            final var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            if (adder != null) {
                adder.add(written);
            }
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(
                argThat(s -> s != null && s.hasRemaining()),
                longThat(p -> p >= 0L),
                notNull(),
                any()
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static void await(final Duration duration) {
        log.debug("awaiting for {}...", duration);
        Awaitility.await()
                .timeout(duration.plusMillis(1L))
                .pollDelay(duration)
                .untilAsserted(() -> Assertions.assertTrue(true));
    }

    public static void awaitForOneSecond() {
        await(Duration.ofSeconds(1L));
    }

    // ---------------------------------------------------------------------------------------------

    private _HelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
