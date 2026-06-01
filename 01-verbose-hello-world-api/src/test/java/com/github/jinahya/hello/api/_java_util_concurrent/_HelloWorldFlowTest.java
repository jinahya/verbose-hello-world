package com.github.jinahya.hello.api._java_util_concurrent;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.nio.*;
import java.nio.charset.*;

/**
 * An abstract class for testing interfaces defined in {@link HelloWorldFlow} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class _HelloWorldFlowTest
        extends HelloWorld__Test {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stub_set_array_will_set_hello_world_bytes_to_the_array() {
        // stub, <service.set(array)> will set 'hello, world' to the <array>
        BDDMockito.willAnswer(i -> {
                    final var array = i.getArgument(0, byte[].class);
                    final var src = "hello, world".getBytes(StandardCharsets.US_ASCII);
                    assert src.length == HelloWorld.BYTES;
                    System.arraycopy(
                            src,
                            0,
                            array,
                            0,
                            src.length
                    );
                    return array;
                })
                .given(service())
                .set(ArgumentMatchers.<byte[]>argThat(
                        a -> a != null && a.length >= HelloWorld.BYTES));
    }

    @BeforeEach
    void stub_put_buffer_will_put_hello_world_bytes_to_the_buffer() {
        // stub, <service.put(buffer)> will put 'hello, world' to the <buffer>
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    final var position = buffer.position();
                    buffer.put(service().set(new byte[HelloWorld.BYTES]));
                    assert buffer.position() == position + HelloWorld.BYTES;
                    return buffer;
                })
                .given(service())
                .<ByteBuffer>put(ArgumentMatchers.argThat(
                        b -> b != null && b.remaining() >= HelloWorld.BYTES));
    }
}
