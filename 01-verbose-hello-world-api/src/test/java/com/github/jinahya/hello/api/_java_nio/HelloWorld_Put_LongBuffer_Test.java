package com.github.jinahya.hello.api._java_nio;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * A class for testing {@link HelloWorld#put(LongBuffer) put(buffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(LongBuffer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Put_LongBuffer_Test
        extends HelloWorldTest {

    private static Stream<ByteOrder> byteOrderStream() {
        return _Java_Nio_TestUtils.byteOrderStream();
    }

    private static Stream<Arguments> namedByteOrderStream() {
        return _Java_Nio_TestUtils.namedByteOrderStream();
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void _ThrowNullPointerException_BufferIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = (LongBuffer) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(buffer)
        );
    }

    @Test
    void _ThrowBufferOverflowException_BufferRemainingIsLessThan12() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = LongBuffer.allocate(
                ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)
        );
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                BufferOverflowException.class,
                () -> service.put(buffer)
        );
    }

    @DisplayName("should invoke append(buffer)")
    @MethodSource({
            "namedByteOrderStream"
    })
    @ParameterizedTest
    void _ShouldPut12Ints_(final ByteOrder byteOrder)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.put_buffer_will_put_actual_hello_world_bytes(
                service()
        );
        final var byteBuffer = ByteBuffer.allocate(HelloWorld.BYTES * Long.BYTES);
        final var buffer = Mockito.spy(byteBuffer.order(byteOrder).asLongBuffer());
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        final var b = HelloWorldTestUtils.put_buffer12_invoked_once(service);
        final var inOrder = Mockito.inOrder(buffer);
        for (int i = 0; i < b.capacity(); i++) {
            inOrder.verify(buffer).put(b.get(i));
        }
        final byte[] fourBytes = new byte[Long.BYTES];
        for (int i = 0; i < buffer.capacity(); i++) {
            byteBuffer.get(i * Long.BYTES, fourBytes);
            log.debug("buffer[{}]: {}, value: {} '{}'",
                      String.format("%1$2d", i),
                      HexFormat.of().formatHex(fourBytes),
                      String.format("%2x", buffer.get(i)),
                      (char) buffer.get(i)
            );
        }
    }
}
