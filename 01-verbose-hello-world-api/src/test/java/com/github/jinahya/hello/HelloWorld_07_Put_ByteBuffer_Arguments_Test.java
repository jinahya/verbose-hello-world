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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_07_Put_ByteBuffer_Test
 */
@DisplayName("put(buffer) arguments")
@Slf4j
class HelloWorld_07_Put_ByteBuffer_Arguments_Test extends _HelloWorldTest {

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
        assertThrows(NullPointerException.class, () -> service.put(buffer));
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
        var service = serviceInstance();
        var buffer = mock(ByteBuffer.class);
        when(buffer.remaining()).thenReturn(current().nextInt(BYTES));
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(BufferOverflowException.class, () -> service.put(buffer));
    }
}
