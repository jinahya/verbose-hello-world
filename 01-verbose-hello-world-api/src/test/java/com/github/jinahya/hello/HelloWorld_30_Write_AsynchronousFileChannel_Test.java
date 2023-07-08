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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_30_Write_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_30_Write_AsynchronousFileChannel_Test extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        stubPutBufferToReturnTheBufferAsItsPositionIncreasedBy12();
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
    @DisplayName("(channel, position) -> channel.write(buffer)+")
    @Test
    void _PutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        when(channel.write(argThat(s -> s != null && s.hasRemaining()), longThat(p -> p >= 0L)))
                .thenAnswer(w -> {
                    var future = mock(Future.class);
                    when(future.get()).thenAnswer(g -> {
                        var src = w.getArgument(0, ByteBuffer.class);
                        var position = w.getArgument(1, Long.class);
                        var written = current().nextInt(src.remaining() + 1);
                        src.position(src.position() + written);
                        writtenSoFar.add(written);
                        return written;
                    });
                    return future;
                });
        var position = current().nextLong(0, Long.MAX_VALUE - BYTES);
        // ------------------------------------------------------------------------------------ WHEN
        service.write(channel, position);
        // ------------------------------------------------------------------------------------ THEN
        verify(service, times(1)).put(bufferCaptor().capture()); // ---------------------- <1>
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        verify(channel, atLeastOnce()).write(same(buffer), positionCaptor().capture()); // <2>
        var positionArguments = positionCaptor().getAllValues();
        assertEquals(position, positionArguments.get(0)); // ----------------------------- <3>
        positionArguments.stream().reduce((p1, p2) -> { // ------------------------------- <4>
            assertTrue(p2 >= p1);
            return p2;
        });
        assertEquals(BYTES, writtenSoFar.intValue()); // --------------------------------- <5>
    }

    /**
     * Verifies {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method returns given {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("(channel, position)channel")
    @Test
    void _ReturnChannel_() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        when(channel.write(argThat(s -> s != null && s.hasRemaining()), longThat(p -> p >= 0L)))
                .thenAnswer(w -> {
                    var future = mock(Future.class);
                    when(future.get()).thenAnswer(g -> {
                        var src = w.getArgument(0, ByteBuffer.class);
                        var written = src.remaining();
                        src.position(src.limit());
                        return written;
                    });
                    return future;
                });
        var position = 0L;
        // ------------------------------------------------------------------------------------ WHEN
        var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ THEN
        assertSame(channel, result);
    }
}
