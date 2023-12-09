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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_61_Write_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_61_Write_AsynchronousFileChannel_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel} starting
     * at {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName(
            "(channel, position) -> put(buffer[12]) -> channel.write(buffer)+")
    @Test
    void _PutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        _stub_ToWriteSome(channel, writtenSoFar);
        var position = current().nextLong(MAX_VALUE - BYTES);
        // ------------------------------------------------------------------------------------ when
        var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).put(bufferCaptor().capture()); // ---------------------- <1>
        var buffer = bufferCaptor().getValue();
        verify(channel, atLeastOnce()).write(same(buffer), positionCaptor().capture()); // <2>
        var positionArguments = positionCaptor().getAllValues(); // -----------------------<3>
        assertEquals(position, positionArguments.get(0)); // ----------------------------- <4>
        var lastPosition = positionArguments.stream().reduce((p1, p2) -> { // ------------ <5>
            assertTrue(p2 >= p1);
            return p2;
        });
        assertTrue(lastPosition.isPresent());
        assertTrue(lastPosition.get() < position + BYTES);
        assertEquals(BYTES, writtenSoFar.intValue()); // --------------------------------- <6>
        assertSame(channel, result);
    }
}
