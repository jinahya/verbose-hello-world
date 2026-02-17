package com.github.jinahya.hello.api;

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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

    /**
     * Returns an array of bytes contains {@code hello, world}.
     *
     * @return an array of bytes contains {@code hello, world}.
     */
    protected static byte[] new_hello_world_array() {
        final var array = "hello, world".getBytes(StandardCharsets.US_ASCII);
        assert array.length == HelloWorld.BYTES;
        return array;
    }

    /**
     * Returns a read-only byte buffer wraps {@link #new_hello_world_array()}.
     *
     * @return a read-only byte buffer wraps {@link #new_hello_world_array()}.
     */
    protected static ByteBuffer new_hello_world_buffer() {
        final var buffer = ByteBuffer.wrap(new_hello_world_array());
        assert buffer.remaining() == HelloWorld.BYTES;
        return buffer.asReadOnlyBuffer();
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    // ------------------------------------------------------------------------------------- service
    protected HelloWorld set_array_will_return_the_array() {
        return HelloWorldTestUtils.set_array_will_return_the_array(service);
    }

    protected HelloWorld set_array_will_set_actual_hello_world_bytes() {
        return HelloWorldTestUtils.set_array_will_set_actual_hello_world_bytes(service);
    }

    protected HelloWorld put_buffer_will_increase_buffer_position_by_12() {
        return HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(service);
    }

    protected ByteBuffer put_buffer12_invoked_once() {
        return HelloWorldTestUtils.put_buffer12_invoked_once(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld service;
}
