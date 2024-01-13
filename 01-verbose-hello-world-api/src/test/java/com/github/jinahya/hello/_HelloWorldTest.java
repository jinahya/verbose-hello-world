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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * An abstract class for testing methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class _HelloWorldTest {

    protected static <C extends WritableByteChannel> C _stub_ToWriteSome(final C channel,
                                                                         final LongAdder adder)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!Mockito.mockingDetails(channel).isMock()) {
            throw new IllegalArgumentException("channel is not a mock: " + channel);
        }
        BDDMockito.given(channel.write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()))).willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            if (adder != null) {
                adder.add(written);
            }
            return written;
        });
        return channel;
    }

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
     * @param <T>     channel type parameter
     * @return given {@code channel}.
     */
    protected static <T extends AsynchronousFileChannel> T _stub_ToWriteSome(
            T channel,
            LongAdder adder) {
        if (!Mockito.mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        Objects.requireNonNull(adder, "adder is null");
        BDDMockito.willAnswer(w -> { // invocation of channel.write
            var future = Mockito.mock(Future.class);
            Mockito.when(future.get()).thenAnswer(g -> { // invocation of future.get
                var src = w.getArgument(0, ByteBuffer.class);
                var position = w.getArgument(1, Long.class);
                var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
                src.position(src.position() + written);
                adder.add(written);
                return written;
            });
            return future;
        }).given(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.longThat(v -> v >= 0L)                       // <position>
        );
        return channel;
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
        if (!Mockito.mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        Objects.requireNonNull(exc, "exc is null");
        BDDMockito.willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var handler = i.getArgument(3, CompletionHandler.class);
            handler.failed(exc, attachment);
            return null;
        }).given(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.longThat(v -> v >= 0L),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        return exc;
    }

    @SuppressWarnings({"unchecked"})
    protected static void _stub_ToComplete(final AsynchronousFileChannel channel,
                                           final LongAdder adder) {
        if (!Mockito.mockingDetails(Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        BDDMockito.willAnswer(i -> {
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
                ArgumentMatchers.argThat(s -> s != null && s.hasRemaining()),
                ArgumentMatchers.longThat(p -> p >= 0L),
                ArgumentMatchers.notNull(),
                ArgumentMatchers.any()
        );
    }

    protected static Throwable _stub_ToFail(AsynchronousByteChannel channel) {
        return _stub_ToFail(channel, new Throwable("just failing"));
    }

    @SuppressWarnings({"unchecked"})
    protected static <T extends Throwable> T _stub_ToFail(
            AsynchronousByteChannel channel, T exc) {
        if (!Mockito.mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        BDDMockito.willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            handler.failed(exc, attachment);
            return null;
        }).given(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),
                // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        return exc;
    }

    protected static <T extends AsynchronousByteChannel> T _stub_ToComplete(
            T channel) {
        return _stub_ToComplete(channel, new LongAdder());
    }

    @SuppressWarnings({"unchecked"})
    protected static <T extends AsynchronousByteChannel> T _stub_ToComplete(T channel,
                                                                            LongAdder adder) {
        if (!Mockito.mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        Objects.requireNonNull(adder, "adder is null");
        BDDMockito.willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            adder.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),
                // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        return channel;
    }

    protected static void _stub_ToWriteSome(final AsynchronousByteChannel channel,
                                            final LongAdder adder) {
        Objects.requireNonNull(channel, "channel is null");
        if (!Mockito.mockingDetails(channel).isMock()) {
            throw new IllegalArgumentException("channel is not a mock: " + channel);
        }
        Objects.requireNonNull(adder, "adder is null");
        BDDMockito.given(channel.write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()))).willAnswer(w -> {
            final var future = Mockito.mock(Future.class);
            BDDMockito.given(future.get()).willAnswer(g -> {
                final var src = w.getArgument(0, ByteBuffer.class);
                final var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
                src.position(src.position() + written);
                adder.add(written);
                return written;
            });
            return future;
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns an instance of {@link Random} for testing.
     *
     * @return an instance of {@link Random}.
     */
    protected final Random random() {
        return ThreadLocalRandom.current();
    }

    // ---------------------------------------------------------------------------------------------
    protected final void writeChannel_willReturnChannel() throws IOException {
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.any(WritableByteChannel.class));
    }

    /**
     * Stubs {@code service}'s {@link HelloWorld#write(OutputStream) write(stream)} method to write
     * {@value HelloWorld#BYTES} bytes to the {@code stream}, and return the {@code stream}.
     */
    protected final void writeStream_willWrite12Bytes() throws IOException {
        BDDMockito.willAnswer(i -> {
            var stream = i.getArgument(0, OutputStream.class);
            stream.write(new byte[HelloWorld.BYTES]);
            return stream;
        }).given(service).write(ArgumentMatchers.any(OutputStream.class));
    }

    // -------------------------------------------------------------------------- set(byte[], index)

    /**
     * Stubs {@code service}'s {@link HelloWorld#set(byte[], int) set(array, index)} method to just
     * return the {@code array} argument.
     */
    @BeforeEach
    final void setArrayIndex_willReturnArray() {
        Mockito.doAnswer(i -> i.getArgument(0))                         // <3>
                .when(service)                                          // <1>
                .set(ArgumentMatchers.any(), ArgumentMatchers.anyInt()) // <2>
        ;
    }

    // --------------------------------------------------------------------------------- set(byte[])

    /**
     * Stubs {@code service}'s {@link HelloWorld#set(byte[]) set(array)} method to return the
     * {@code buffer} after accepted to specified consumer.
     *
     * @param consumer the consumer accepts the {@code array}.
     */
    protected final void setArray_willReturnArray(final Consumer<? super byte[]> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        Mockito.doAnswer(i -> {
                    final var array = i.getArgument(0, byte[].class);
                    consumer.accept(array);
                    return array;
                })
                .when(service)
                .set(ArgumentMatchers.any());
    }

    /**
     * Stubs {@code service}'s {@link HelloWorld#set(byte[]) set(array)} method to just return the
     * {@code array}.
     *
     * @implSpec This method invokes {@link #setArray_willReturnArray(Consumer)} method with a
     * consumer does nothing.
     */
    protected final void setArray_willReturnArray() {
        setArray_willReturnArray(a -> {
            // does nothing
        });
    }

    // ------------------------------------------------------------------------------- write(stream)

    /**
     * Stubs {@code service}'s {@link HelloWorld#write(OutputStream) write(stream)} method to just
     * return the {@code stream} argument.
     */
    protected final void writeStream_willReturnStream() throws IOException {
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.any(OutputStream.class));
    }

    // --------------------------------------------------------------------------------- put(buffer)

    /**
     * Stubs {@code service}'s {@link HelloWorld#put(ByteBuffer) put(buffer)} method to return the
     * {@code buffer} after accepted to specified consumer.
     *
     * @param consumer the consumer accepts the {@code buffer} argument.
     */
    protected final void putBuffer_willReturnTheBuffer(
            final Consumer<? super ByteBuffer> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        BDDMockito.doAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    consumer.accept(buffer);
                    return buffer;
                })
                .when(service)
                .put(ArgumentMatchers.any());
    }

    /**
     * Stubs {@code service}'s {@link HelloWorld#put(ByteBuffer) put(buffer)} method to return the
     * {@code buffer} as its position increased by {@value HelloWorld#BYTES}.
     */
    protected final void putBuffer_willReturnTheBuffer_asItsPositionIncreasedBy12() {
        putBuffer_willReturnTheBuffer(b -> {
            if (b != null) {
                b.position(b.position() + HelloWorld.BYTES);
            }
        });
//        BDDMockito.doAnswer(i -> {
//                    final var buffer = i.getArgument(0, ByteBuffer.class);
//                    buffer.position(buffer.position() + HelloWorld.BYTES);
//                    return buffer;
//                })
//                .when(service)
//                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
    }

    // ---------------------------------------------------------------------------------------------
    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld service;

    // ---------------------------------------------------------------------------------------------

    /**
     * A captor for capturing the {@code array} argument.
     *
     * @see HelloWorld#set(byte[])
     * @see HelloWorld#set(byte[], int)
     * @see #indexCaptor
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<byte[]> arrayCaptor;

    /**
     * A captor for capturing the {@code index} argument.
     *
     * @see HelloWorld#set(byte[], int)
     * @see #arrayCaptor
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<Integer> indexCaptor;

    /**
     * An argument captor for capturing an argument of {@link OutputStream}.
     *
     * @see HelloWorld#write(OutputStream)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<OutputStream> streamCaptor;

    /**
     * An argument captor for capturing an argument of {@link ByteBuffer}.
     *
     * @see HelloWorld#put(ByteBuffer)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<ByteBuffer> bufferCaptor;

    /**
     * A captor for capturing an argument of {@link WritableByteChannel}.
     *
     * @see HelloWorld#write(WritableByteChannel)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<WritableByteChannel> channelCaptor;

    /**
     * A captor for capturing the {@code position} argument of
     * {@link HelloWorld#write(AsynchronousFileChannel, long)} method.
     *
     * @see HelloWorld#write(AsynchronousFileChannel, long)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<Long> positionCaptor;
}
