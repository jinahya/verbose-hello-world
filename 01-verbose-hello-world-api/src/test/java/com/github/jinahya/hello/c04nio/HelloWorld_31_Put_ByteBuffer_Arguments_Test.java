package com.github.jinahya.hello.c04nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer) put(buffer)} method regarding arguments
 * verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_31_Put_ByteBuffer_Test
 */
@DisplayName("put(buffer) arguments")
@Slf4j
class HelloWorld_31_Put_ByteBuffer_Arguments_Test extends _HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link NullPointerException} when the {@code buffer} argument is {@code null}.
     */
    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_BufferIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var buffer = (ByteBuffer) null;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> service.put(buffer)
        );
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link BufferOverflowException} when {@link ByteBuffer#remaining() buffer.remaining} is less
     * than {@value HelloWorld#BYTES}.
     */
    @DisplayName("(buffer.remaining < 12)BufferOverflowException")
    @Test
    void _ThrowBufferOverflowException_BufferRemainingIsLessThan12() {
        // ----------------------------------------------------------------------------------- GIVEN
        final var service = serviceInstance();
        final ByteBuffer buffer;
        {
            final var capacity = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES);
            buffer = ByteBuffer.allocate(capacity);
        }
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThatCode(() -> service.put(buffer))
                .isInstanceOf(BufferOverflowException.class);
    }
}