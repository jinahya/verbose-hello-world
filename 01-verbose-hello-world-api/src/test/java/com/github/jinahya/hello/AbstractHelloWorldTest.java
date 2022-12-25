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
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.verification.VerificationMode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
abstract class AbstractHelloWorldTest<T extends HelloWorld> {

    AbstractHelloWorldTest(final Class<T> serviceClass) {
        super();
        service = spy(requireNonNull(serviceClass, "serviceClass is null"));
    }

    /**
     * Stubs {@link HelloWorld#set(byte[], int) set(array, index)} method to return the
     * {@code array} argument.
     */
    @BeforeEach
    void stub_SetArrayIndex_ReturnArray() {
        when(service.set(any(), anyInt()))            // <1>
                .thenAnswer(i -> i.getArgument(0));   // <2>
    }

    void verify_SetArrayIndex_Invoked(VerificationMode mode,
                                      Consumer<? super byte[]> arrayConsumer,
                                      IntConsumer indexConsumer) {
        verify(service, mode).set(arrayCaptor.capture(), indexCaptor.getValue());
        var array = arrayCaptor.getValue();
        var index = indexCaptor.getValue();
        arrayConsumer.accept(array);
        indexConsumer.accept(index);
    }

    void verify_SetArrayIndex_Invoked_Once(Consumer<? super byte[]> arrayConsumer,
                                           IntConsumer indexConsumer) {
        verify_SetArrayIndex_Invoked(times(1), arrayConsumer, indexConsumer);
    }

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer) put(buffer)} method to return the {@code buffer}
     * argument with its {@link ByteBuffer#position() position} increased by
     * {@value HelloWorld#BYTES}.
     */
    void stub_PutBuffer_IncreaseBufferPositionBy12() {
        lenient().doAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + BYTES);
                    return buffer;
                })
                .when(service)
                .put(argThat(b -> b != null && b.remaining() >= BYTES));
    }

    ByteBuffer verify_PutBuffer12_Invoked(VerificationMode mode) {
        verify(service, mode).put(bufferCaptor.capture());
        var buffer = bufferCaptor.getValue();
        assertNotNull(buffer);
        assertEquals(BYTES, buffer.capacity());
        assertFalse(buffer.hasRemaining());
        return buffer;
    }

    ByteBuffer verify_PutBuffer12_Invoked_Once() {
        return verify_PutBuffer12_Invoked(times(1));
    }

    WritableByteChannel verify_WriteChannel_Invoked(VerificationMode mode) throws IOException {
        verify(service, mode).write(channelCaptor.capture());
        var channel = channelCaptor.getValue();
        assertNotNull(channel);
        return channel;
    }

    WritableByteChannel verify_WriteChannel_Invoked_Once() throws IOException {
        return verify_WriteChannel_Invoked(times(1));
    }

    //@Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final T service;

    /**
     * An argument captor for capturing arguments of {@code byte[]}.
     *
     * @see HelloWorld#set(byte[])
     * @see HelloWorld#set(byte[], int)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<byte[]> arrayCaptor;

    /**
     * An argument captor for capturing arguments of {@code int}.
     *
     * @see HelloWorld#set(byte[], int)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<Integer> indexCaptor;

    /**
     * An argument captor for capturing arguments of {@link OutputStream}.
     *
     * @see HelloWorld#write(OutputStream)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<OutputStream> streamCaptor;

    /**
     * An argument captor for capturing arguments of {@link ByteBuffer}.
     *
     * @see HelloWorld#put(ByteBuffer)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<ByteBuffer> bufferCaptor;

    /**
     * An argument captor for capturing arguments of {@link WritableByteChannel}.
     *
     * @see HelloWorld#write(WritableByteChannel)
     */
    @Captor
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private ArgumentCaptor<WritableByteChannel> channelCaptor;
}
