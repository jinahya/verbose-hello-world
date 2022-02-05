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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method regarding
 * arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_07_Put_ByteBuffer_Test
 */
@Slf4j
class HelloWorld_07_Put_ByteBuffer_Arguments_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link NullPointerException} when the {@code buffer} argument is {@code
     * null}.
     */
    @DisplayName("put(null) throws NullPointerException")
    @Test
    void put_ThrowNullPointerException_BufferIsNull() {
        final ByteBuffer buffer = null;
        Assertions.assertThrows(NullPointerException.class,
                                () -> helloWorld().put(buffer));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link BufferOverflowException} when {@link ByteBuffer#remaining()
     * buffer.remaining} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer) throws BufferOverflowException"
                 + " when buffer.remaining is not enough")
    @Test
    void put_ThrowBufferOverflowException_BufferRemainingIsNotEnough() {
        // mock-maker-inline
        final ByteBuffer buffer = Mockito.spy(ByteBuffer.allocate(0));
        Mockito.when(buffer.remaining())
                .thenReturn(new Random().nextInt(HelloWorld.BYTES));
        Assertions.assertThrows(BufferOverflowException.class,
                                () -> helloWorld().put(buffer));
    }
}