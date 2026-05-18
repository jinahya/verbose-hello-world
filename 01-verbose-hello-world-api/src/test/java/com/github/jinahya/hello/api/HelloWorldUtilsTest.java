package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

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

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldUtilsTest extends HelloWorldTest {

    @BeforeEach
    void __() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        HelloWorldTestUtils.put_buffer_will_put_actual_hello_world_bytes(service());
    }

    // --------------------------------------------------------------------------------------- array
    @Test
    @DisplayName("array(service) → byte[12] of \"hello, world\"")
    void array__() {
        // ----------------------------------------------------------------------------------- given
        // ------------------------------------------------------------------------------------ when
        final var result = HelloWorldUtils.array(service());
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(HelloWorld.BYTES, result.length);
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), result);
    }

    // -------------------------------------------------------------------------------------- buffer
    @Test
    @DisplayName("buffer(service) → ByteBuffer of \"hello, world\", ready for reading")
    void buffer__() {
        // ----------------------------------------------------------------------------------- given
        // ------------------------------------------------------------------------------------ when
        final var buffer = HelloWorldUtils.buffer(service());
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(0, buffer.position());
        Assertions.assertEquals(HelloWorld.BYTES, buffer.limit());
        Assertions.assertEquals(HelloWorld.BYTES, buffer.remaining());
        final var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), bytes);
    }

    // ----------------------------------------------------------------------------- stringFromArray
    @Test
    void stringFromArray__() {
        final var string = new String(HelloWorldUtils.array(service()), StandardCharsets.US_ASCII);
    }

    // ---------------------------------------------------------------------------- stringFromBuffer
    @Test
    void stringFromBuffer__() {
        final var string = StandardCharsets.US_ASCII.decode(HelloWorldUtils.buffer(service()));
    }
}
