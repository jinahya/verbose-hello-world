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
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class _HelloWorldTestHelper {

    private static <T> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("not a mock: " + object);
        }
        return object;
    }

    private static <T> T requireSpy(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!mockingDetails(object).isSpy()) {
            throw new IllegalArgumentException("not a spy: " + object);
        }
        return object;
    }

    /**
     * Stubs specified channel whose {@link WritableByteChannel#write(ByteBuffer) write(src)} method
     * increases the {@code src}'s position by random value.
     *
     * @param channel      the channel whose {@link WritableByteChannel#write(ByteBuffer)} method
     *                     are stubbed.
     * @param writtenSoFar an adder for accumulating written bytes; may be {@code null}.
     * @param <C>          channel type parameter
     * @return given {@code channel}.
     * @throws IOException if an I/O error occurs.
     */
    public static <C extends WritableByteChannel> C stub_WriteBuffer_ToWriteSome(
            final C channel, final LongAdder writtenSoFar)
            throws IOException {
        requireMock(channel);
        given(channel.write(argThat(b -> b != null && b.hasRemaining()))).willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            if (writtenSoFar != null) {
                writtenSoFar.add(written);
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
        if (!mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        Objects.requireNonNull(adder, "adder is null");
        willAnswer(w -> { // invocation of channel.write
            var future = mock(Future.class);
            when(future.get()).thenAnswer(g -> { // invocation of future.get
                var src = w.getArgument(0, ByteBuffer.class);
                var position = w.getArgument(1, Long.class);
                var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
                src.position(src.position() + written);
                adder.add(written);
                return written;
            });
            return future;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()), // <src>
                longThat(v -> v >= 0L)                       // <position>
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

    protected static Throwable _stub_ToFail(AsynchronousByteChannel channel) {
        return _stub_ToFail(channel, new Throwable("just failing"));
    }

    @SuppressWarnings({"unchecked"})
    protected static <T extends Throwable> T _stub_ToFail(
            AsynchronousByteChannel channel, T exc) {
        if (!mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            handler.failed(exc, attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()), // <src>
                any(),
                // <attachment>
                notNull()                                    // <handler>
        );
        return exc;
    }

    protected static <T extends AsynchronousByteChannel> T _stub_ToComplete(
            T channel) {
        return _stub_ToComplete(channel, new LongAdder());
    }

    @SuppressWarnings({"unchecked"})
    protected static <T extends AsynchronousByteChannel> T _stub_ToComplete(
            T channel,
            LongAdder adder) {
        if (!mockingDetails(
                Objects.requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        Objects.requireNonNull(adder, "adder is null");
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            var written = ThreadLocalRandom.current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            adder.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()), // <src>
                any(),
                // <attachment>
                notNull()                                    // <handler>
        );
        return channel;
    }

    protected static void _stub_ToWriteSome(final AsynchronousByteChannel channel,
                                            final LongAdder adder) {
        Objects.requireNonNull(channel, "channel is null");
        if (!mockingDetails(channel).isMock()) {
            throw new IllegalArgumentException("channel is not a mock: " + channel);
        }
        Objects.requireNonNull(adder, "adder is null");
        given(channel.write(argThat(b -> b != null && b.hasRemaining()))).willAnswer(w -> {
            final var future = mock(Future.class);
            given(future.get()).willAnswer(g -> {
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
     * Stubs {@code serviceInstance()}'s {@link HelloWorld#put(ByteBuffer) put(buffer[12])} method
     * to return given {@code buffer} as its position increased by {@value HelloWorld#BYTES}.
     */
    protected void _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12() {
        willAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            buffer.position(buffer.limit());
            return buffer;
        }).given(service).put(
                argThat(b -> b != null && b.capacity() == BYTES
                             && b.remaining() == BYTES)
        );
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#write(OutputStream) write(stream)} method
     * to write {@value HelloWorld#BYTES} bytes to the {@code stream}, and return the
     * {@code stream}.
     */
    protected final void _stub_WriteStream_ToWrite12BytesAndReturnTheStream() throws IOException {
        willAnswer(i -> {
            var stream = i.getArgument(0, OutputStream.class);
            stream.write(new byte[BYTES]);
            return stream;
        }).given(service).write(notNull(OutputStream.class));
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#set(byte[], int) set(array, index)} method
     * to just return the {@code array} argument.
     */
    @BeforeEach
    void _stub_SetArrayWithIndex_ToReturnTheArray() {
        when(service.set(any(byte[].class),
                         anyInt()))  // <1>
                .thenAnswer(i -> i.getArgument(0)); // <2>
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#set(byte[]) set(array)} method to just
     * return the {@code array}.
     */
    protected final void _stub_SetArray_ToReturnTheArray() {
        doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(notNull(byte[].class));
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#print(char[]) print(chars)} method to just
     * return the {@code chars}.
     */
    protected void _stub_PrintChars_ToReturnTheChars() {
        doAnswer(i -> i.getArgument(0))
                .when(service)
                .print(any(char[].class));
    }

    /**
     * Stubs {@code service}'s {@link HelloWorld#print(char[], int) print(chars, index)} method to
     * just return the {@code chars} argument.
     */
    protected final void _stub_PrintCharsWithIndex_ToReturnTheChars() {
        doAnswer(i -> i.getArgument(0))              // <1>
                .when(service)                       // <2>
                .print(any(char[].class), anyInt()); // <3>
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
     * A captor for capturing the {@code chars} argument.
     *
     * @see HelloWorld#print(char[])
     * @see HelloWorld#print(char[], int)
     * @see #offsetCaptor
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<char[]> charsCaptor;

    /**
     * A captor for capturing the {@code offset} argument.
     *
     * @see HelloWorld#print(char[], int)
     * @see #charsCaptor
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<Integer> offsetCaptor;

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

    // ---------------------------------------------------------------------------------------------

    private _HelloWorldTestHelper() {
        throw new AssertionError("instantiation is not allowed");
    }
}
