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
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class _HelloWorldTestUtils {

    private static <T> T requireMock(final T object) {
        if (!mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("not a mock: " + object);
        }
        return object;
    }

    private static <T> T requireSpy(final T object) {
        if (!mockingDetails(object).isSpy()) {
            throw new IllegalArgumentException("not a spy: " + object);
        }
        return object;
    }

    // ------------------------------------------------------------------------- WritableByteChannel

    /**
     * Stubs specified channel whose {@link WritableByteChannel#write(ByteBuffer) write(src)} method
     * increases the {@code src}'s position by random amount.
     *
     * @param channel the channel whose {@link WritableByteChannel#write(ByteBuffer) write(src)}
     *                method are stubbed.
     * @param adder   an adder for accumulating written bytes; may be {@code null}.
     * @throws IOException if an I/O error occurs.
     */
    public static void writeBuffer_willWriteSome(final WritableByteChannel channel,
                                                 final LongAdder adder)
            throws IOException {
        requireMock(Objects.requireNonNull(channel, "channel is null"));
        BDDMockito.given(channel.write(argThat(b -> b != null && b.hasRemaining())))
                .willAnswer(i -> {
                    final var src = i.getArgument(0, ByteBuffer.class);
                    final var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
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
     * return a future increases the {@code src}'s position by random value.
     *
     * @param channel the channel whose {@link WritableByteChannel#write(ByteBuffer)} method are
     *                stubbed.
     * @param adder   an adder for accumulating written bytes; may be {@code null}.
     */
    public static void write_willReturnFutureSucceeds(final AsynchronousByteChannel channel,
                                                      final LongAdder adder) {
        requireMock(channel);
        final var previousFuture = new Future[1];
        BDDMockito.given(channel.write(argThat(b -> b != null && b.hasRemaining())))
                .willAnswer(w -> {
                    if (previousFuture[0] != null) {
                        Mockito.verify(previousFuture[0], Mockito.times(1)).get();
                    }
                    final var src = w.getArgument(0, ByteBuffer.class);
                    final var future = Mockito.mock(Future.class);
                    BDDMockito.given(future.get()).willAnswer(g -> {
                        final var written =
                                ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
                        src.position(src.position() + written);
                        if (adder != null) {
                            adder.add(written);
                        }
                        return written;
                    });
                    previousFuture[0] = future;
                    return future;
                });
    }

    public static void write_willReturnFutureFails(final AsynchronousByteChannel channel) {
        requireMock(channel);
        BDDMockito.given(channel.write(argThat(b -> b != null && b.hasRemaining())))
                .willAnswer(w -> {
                    final var future = Mockito.mock(Future.class);
                    BDDMockito.given(future.get())
                            .willThrow(new ExecutionException(new RuntimeException()));
                    return future;
                });
    }

    @SuppressWarnings({
            "unchecked"
    })
    public static void writeWithHandler_completes(final AsynchronousByteChannel channel,
                                                  final LongAdder adder) {
        requireMock(channel);
        Mockito.doAnswer(i -> {
                    final var src = i.getArgument(0, ByteBuffer.class);
                    final var attachment = i.getArgument(1);
                    final var handler = i.getArgument(2, CompletionHandler.class);
                    new Thread(() -> {
                        final var w = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
                        src.position(src.position() + w);
                        handler.completed(w, attachment);
                        if (adder != null) {
                            adder.add(w);
                        }
                    }).start();
                    return null;
                })
                .when(channel).write(
                        argThat(b -> b != null && b.hasRemaining()),
                        any(),
                        notNull()
                );
    }

    @SuppressWarnings({"unchecked"})
    public static void writeWithHandler_fails(final AsynchronousByteChannel given) {
        requireMock(Objects.requireNonNull(given, "given is null"));
        BDDMockito.willAnswer(i -> {
                    final var attachment = i.getArgument(1);
                    final var handler = i.getArgument(2, CompletionHandler.class);
                    handler.failed(new Exception(), attachment);
                    return null;
                })
                .given(given).write(
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
     * @param channel      the channel whose
     *                     {@link AsynchronousFileChannel#write(ByteBuffer, long) write(src,
     *                     position)} method is stubbed.
     * @param writtenSoFar the adder to which the number of written bytes is added.
     */
    public static void _stub_ToWriteSome(final AsynchronousFileChannel channel,
                                         final LongAdder writtenSoFar) {
        if (!mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        Objects.requireNonNull(writtenSoFar, "adder is null");
        willAnswer(w -> { // invocation of channel.write
            var future = Mockito.mock(Future.class);
            when(future.get()).thenAnswer(g -> { // invocation of future.get
                var src = w.getArgument(0, ByteBuffer.class);
                var position = w.getArgument(1, Long.class);
                var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
                src.position(src.position() + written);
                writtenSoFar.add(written);
                return written;
            });
            return future;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()), // <src>
                longThat(v -> v >= 0L)                       // <position>
        );
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
    protected static <T extends Throwable> T _stub_ToFail(
            AsynchronousFileChannel channel,
            final T exc) {
        if (!mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
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
        if (!mockingDetails(Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            final var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
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

    // ---------------------------------------------------------------------------------------------

    private _HelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
