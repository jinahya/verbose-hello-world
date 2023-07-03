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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
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
abstract class __AbstractHelloWorldTest<T extends HelloWorld> {

    /**
     * Creates a new instance for testing specified class.
     *
     * @param serviceClass the class to test.
     */
    __AbstractHelloWorldTest(final Class<T> serviceClass) {
        super();
        serviceInstance = spy(requireNonNull(serviceClass, "serviceClass is null"));
    }

    @SuppressWarnings({"unchecked"})
    void stubToFail(AsynchronousByteChannel channel, LongAdder adder) {
        willAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src != null : "src should not be null";
            assert src.hasRemaining() : "src should have remaining";
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            assert handler != null : "handler should not be null";
            handler.failed(new Throwable("just failing"), attachment);
            return null;
        }).given(channel).write(any(), any(), any());
    }

    void stubToComplete(AsynchronousByteChannel channel, LongAdder adder) {
        willAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src != null : "src should not be null";
            assert src.hasRemaining() : "src should have remaining";
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            assert handler != null : "handler should not be null";
            var written = current().nextInt(1, src.remaining() + 1);
            src.position(src.position() + written);
            adder.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(any(), any(), any());
    }

    /**
     * Stubs {@link #serviceInstance}'s {@link HelloWorld#put(ByteBuffer) put(buffer)} method to
     * return given {@code buffer} as its position increased by {@value HelloWorld#BYTES}.
     */
    void stubPutBufferToReturnTheBufferAsItsPositionIncreasedBy12() {
        doAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null : "buffer should not be null";
            buffer.position(buffer.position() + BYTES); // IllegalArgumentException
            return buffer;
        }).when(serviceInstance()).put(any());
    }

    /**
     * Stubs {@link HelloWorld#set(byte[], int) set(array, index)} method to just return the
     * {@code array} argument.
     */
    @DisplayName("[stubbing] set(array, index) returns array")
    @BeforeEach
    void _ReturnArray_SetArrayIndex() {
        when(serviceInstance().set(any(), anyInt()))        // <1>
                .thenAnswer(i -> i.getArgument(0)); // <2>
    }

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final T serviceInstance;

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
     * An argument captor for capturing an argument of {@link WritableByteChannel}.
     *
     * @see HelloWorld#write(WritableByteChannel)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<WritableByteChannel> channelCaptor;
}
