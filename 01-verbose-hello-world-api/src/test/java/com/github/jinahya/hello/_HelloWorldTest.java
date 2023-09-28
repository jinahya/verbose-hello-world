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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * An abstract class for testing methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@Slf4j
public abstract class _HelloWorldTest {

    protected static <T extends WritableByteChannel> T _stub_ToWriteSome(
            T channel, LongAdder adder)
            throws IOException {
        if (!mockingDetails(
                requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            if (adder != null) {
                adder.add(written);
            }
            return written;
        }).given(channel).write(argThat(b -> b != null && b.hasRemaining()));
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
                requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        requireNonNull(adder, "adder is null");
        willAnswer(w -> { // invocation of channel.write
            var future = mock(Future.class);
            when(future.get()).thenAnswer(g -> { // invocation of future.get
                var src = w.getArgument(0, ByteBuffer.class);
                var position = w.getArgument(1, Long.class);
                var written = current().nextInt(1, src.remaining() + 1);
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
                requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        requireNonNull(exc, "exc is null");
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
                ArgumentMatchers.any(),
                notNull()
        );
        return exc;
    }

    @SuppressWarnings({"unchecked"})
    protected static void _stub_ToComplete(AsynchronousFileChannel channel,
                                           LongAdder adder) {
        if (!mockingDetails(
                requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var handler = i.getArgument(3, CompletionHandler.class);
            var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            if (adder != null) {
                adder.add(written);
            }
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()),
                longThat(v -> v >= 0L),
                ArgumentMatchers.any(),
                notNull()
        );
    }

    protected static Throwable _stub_ToFail(AsynchronousByteChannel channel) {
        return _stub_ToFail(channel, new Throwable("just failing"));
    }

    @SuppressWarnings({"unchecked"})
    protected static <T extends Throwable> T _stub_ToFail(
            AsynchronousByteChannel channel, T exc) {
        if (!mockingDetails(
                requireNonNull(channel, "channel is null")).isMock()) {
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
                ArgumentMatchers.any(),
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
                requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        requireNonNull(adder, "adder is null");
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            adder.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),
                // <attachment>
                notNull()                                    // <handler>
        );
        return channel;
    }

    protected static void _stub_ToWriteSome(AsynchronousByteChannel channel,
                                            LongAdder adder) {
        if (!mockingDetails(
                requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        requireNonNull(adder, "adder is null");
        willAnswer(w -> {
            var future = mock(Future.class);
            when(future.get()).thenAnswer(g -> {
                var src = w.getArgument(0, ByteBuffer.class);
                var written = current().nextInt(1, src.remaining() + 1);
                src.position(src.position() + written);
                adder.add(written);
                return written;
            });
            return future;
        }).given(channel).write(argThat(b -> b != null && b.hasRemaining()));
    }

    /**
     * Creates a new instance for testing specified class.
     */
    protected _HelloWorldTest() {
        super();
    }

    /**
     * Stubs {@code serviceInstance()}'s {@link HelloWorld#put(ByteBuffer) put(buffer[12])} method
     * to return given {@code buffer} as its position increased by {@value HelloWorld#BYTES}.
     */
    protected void _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12() {
        willAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            buffer.position(buffer.limit());
            return buffer;
        }).given(serviceInstance).put(
                argThat(b -> b != null && b.capacity() == BYTES
                             && b.remaining() == BYTES)
        );
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#write(OutputStream) write(stream)} method
     * to write {@value HelloWorld#BYTES} bytes to the {@code stream}, and return the
     * {@code stream}.
     */
    protected void _stub_WriteStream_ToWrite12BytesAndReturnTheStream()
            throws IOException {
        willAnswer(i -> {
            var stream = i.getArgument(0, OutputStream.class);
            stream.write(new byte[BYTES]);
            return stream;
        }).given(serviceInstance).write(notNull(OutputStream.class));
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#set(byte[]) set(array)} method to just
     * return the {@code array}.
     */
    protected void _stub_SetArray_ToReturnTheArray() {
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(serviceInstance)
                .set(ArgumentMatchers.any(byte[].class));
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#set(byte[], int) set(array, index)} method
     * to just return the {@code array} argument.
     */
    @BeforeEach
    void _stub_SetArrayWithIndex_ToReturnTheArray() {
        when(serviceInstance.set(ArgumentMatchers.any(byte[].class),
                                 anyInt()))  // <1>
                .thenAnswer(i -> i.getArgument(0)); // <2>
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#print(char[]) print(chars)} method to just
     * return the {@code chars}.
     */
    protected void _stub_PrintChars_ToReturnTheChars() {
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(serviceInstance)
                .print(ArgumentMatchers.any(char[].class));
    }

    /**
     * Stubs {@code serviceInstance}'s {@link HelloWorld#print(char[], int) print(chars, offset)}
     * method to just return the {@code chars} argument.
     */
    protected void _stub_PrintCharsWithOffset_ToReturnTheChars() {
        Mockito.doAnswer(i -> i.getArgument(0))            // <1>
                .when(serviceInstance)             // <2>
                .print(ArgumentMatchers.any(char[].class), anyInt()); // <1>
    }

    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld serviceInstance;

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
}
