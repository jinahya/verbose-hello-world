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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.ByteBuffer;

/**
 * An abstract class for testing methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/verbose-hello-world/issues/4">[#4] Implement HelloWorld
 * interface</a> (GitHub)
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class HelloWorldTest {

    // ------------------------------------------------------------------------------------- service

    /**
     * Stubs {@link HelloWorld#set(byte[]) service#set(array)} method to return the {@code array}.
     *
     * @see #verify_set_array12_invoked_once()
     */
    protected final void stub_set_array_will_return_the_array() {
        BDDMockito.willAnswer(i -> i.getArgument(0))
                .given(service)
                .set(ArgumentMatchers.any());
    }

    /**
     * Verifies that {@link HelloWorld#set(byte[]) set(array)} method invoked on the
     * {@link #service() service} instance with an array of {@value HelloWorld#BYTES} bytes.
     *
     * @return the {@code array} argument captured.
     * @see #stub_set_array_will_return_the_array()
     */
    protected final byte[] verify_set_array12_invoked_once() {
        final var captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(service, Mockito.times(1)).set(captor.capture());
        final var array = captor.getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        return array;
    }

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer) service#put(buffer)} method to increase the
     * {@code buffer}'s {@code position} by {@value HelloWorld#BYTES}.
     *
     * @see #verify_put_buffer12_invoked_once()
     */
    protected final void stub_put_buffer_will_increase_buffer_position_by_12() {
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
    }

    /**
     * Verifies that {@link HelloWorld#put(ByteBuffer) put(buffer)} method invoked on the
     * {@link #service() service} instance with a byte buffer of {@value HelloWorld#BYTES} bytes.
     *
     * @return the {@code buffer} argument captured.
     * @see #stub_put_buffer_will_increase_buffer_position_by_12()
     */
    protected final ByteBuffer verify_put_buffer12_invoked_once() {
        final var captor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(captor.capture());
        final var buffer = captor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        return buffer;
    }

    // ---------------------------------------------------------------------------------------------
    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld service;
}
