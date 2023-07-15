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
import org.mockito.Captor;
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
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doAnswer;
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
abstract class _HelloWorldTest {

    /**
     * Creates a new instance for testing specified class.
     */
    _HelloWorldTest() {
        super();
    }

    /**
     * Stubs specified channel, on its
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * write(channel, position, attachment, handler)} method invokes
     * {@code handler.failed(exc, attachment)}.
     *
     * @param channel the channel to stub.
     */
    @SuppressWarnings({"unchecked"})
    void stubToFail(AsynchronousFileChannel channel) {
        if (!mockingDetails(requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var handler = i.getArgument(3, CompletionHandler.class);
            handler.failed(new Throwable("just failing"), attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()),
                longThat(v -> v >= 0L),
                any(),
                notNull()
        );
    }

    @SuppressWarnings({"unchecked"})
    void stubToComplete(AsynchronousFileChannel channel, LongAdder adder) {
        if (!mockingDetails(requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        requireNonNull(adder, "adder is null");
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var handler = i.getArgument(3, CompletionHandler.class);
            var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            adder.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(
                argThat(b -> b != null && b.hasRemaining()),
                longThat(v -> v >= 0L),
                any(),
                notNull()
        );
    }

    Throwable _stub_ToFail(AsynchronousByteChannel channel) {
        return _stub_ToFail(channel, new Throwable("just failing"));
    }

    @SuppressWarnings({"unchecked"})
    <T extends Throwable> T _stub_ToFail(AsynchronousByteChannel channel, T exc) {
        if (!mockingDetails(requireNonNull(channel, "channel is null")).isMock()) {
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
                any(),                                       // <attachment>
                notNull()                                    // <handler>
        );
        return exc;
    }

    <T extends AsynchronousByteChannel> T _stub_ToComplete(T channel) {
        return _stub_ToComplete(channel, mock(LongAdder.class));
    }

    @SuppressWarnings({"unchecked"})
    <T extends AsynchronousByteChannel> T _stub_ToComplete(T channel, LongAdder adder) {
        if (!mockingDetails(requireNonNull(channel, "channel is null")).isMock()) {
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
                any(),                                       // <attachment>
                notNull()                                    // <handler>
        );
        return channel;
    }

    @SuppressWarnings({"unchecked"})
    void stubToWriteSome(WritableByteChannel channel, LongAdder adder) throws IOException {
        if (!mockingDetails(requireNonNull(channel, "channel is null")).isMock()) {
            throw new IllegalArgumentException("not a mock: " + channel);
        }
        requireNonNull(adder, "adder is null");
        willAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            adder.add(written);
            return written;
        }).given(channel).write(argThat(b -> b != null && b.hasRemaining()));
    }

    /**
     * Stubs {@link #serviceInstance()} as its {@link HelloWorld#put(ByteBuffer) put(buffer[12])}
     * method to return given {@code buffer} as its position increased by
     * {@value HelloWorld#BYTES}.
     */
    void _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12() {
        doAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            buffer.position(buffer.limit());
            return buffer;
        }).when(serviceInstance)
                .put(argThat(b -> b != null && b.capacity() == BYTES && b.remaining() == BYTES));
    }

    /**
     * Stubs {@link #serviceInstance() serviceInstance}'s
     * {@link HelloWorld#set(byte[]) set(array[12])} method to just return the {@code array}.
     */
    @BeforeEach
    void _stub_SetArray_ToReturnTheArray() {
        when(serviceInstance.set(argThat(a -> a != null && a.length == BYTES))) // <1>
                .thenAnswer(i -> i.getArgument(0));                             // <2>
    }

    /**
     * Stubs {@link #serviceInstance() serviceInstance}'s
     * {@link HelloWorld#set(byte[], int) set(array, index)} method to just return the {@code array}
     * argument.
     */
    @BeforeEach
    void stubSetArrayWithIndexToReturnTheArray() {
        when(serviceInstance.set(any(), anyInt()))  // <1>
                .thenAnswer(i -> i.getArgument(0)); // <2>
    }

    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private HelloWorld serviceInstance;

    /**
     * An argument captor for capturing an argument of {@code byte[]}.
     *
     * @see HelloWorld#set(byte[])
     * @see HelloWorld#set(byte[], int)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<byte[]> arrayCaptor;

    /**
     * An argument captor for capturing an argument of {@code int}.
     *
     * @see HelloWorld#set(byte[], int)
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
