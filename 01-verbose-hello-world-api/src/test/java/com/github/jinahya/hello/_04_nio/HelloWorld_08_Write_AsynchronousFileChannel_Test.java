package com.github.jinahya.hello._04_nio;

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
import com.github.jinahya.hello._HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_08_Write_AsynchronousFileChannel_Test extends _HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the [channel] argument is [null]"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousFileChannel) null;
        final var position = 0L;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link IllegalArgumentException} when the {@code position} argument is negative.
     */
    @DisplayName("""
            should throw an IllegalArgumentException
            when the [position] argument is [not positive]"""
    )
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel, position)
        );
    }

    @BeforeEach
    void _beforeEach() {
        putBuffer_willReturnTheBuffer_asItsPositionIncreasedBy12();
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
    void _PutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    if (buffer != null && buffer.remaining() >= HelloWorld.BYTES) {
                        buffer.position(buffer.position() + HelloWorld.BYTES);
                    }
                    return buffer;
                })
                .given(service).put(ArgumentMatchers.any());
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var writtenSoFar = new LongAdder();
        _HelloWorldTestUtils.writeBuffer_willWriteSome(channel, writtenSoFar);
        final var position = ThreadLocalRandom.current()
                .nextLong(Long.MAX_VALUE - HelloWorld.BYTES);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor().capture());
        final var buffer = bufferCaptor().getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        Mockito.verify(channel, Mockito.atLeastOnce())
                .write(ArgumentMatchers.same(buffer), positionCaptor().capture()); // ---------- <1>
        final var positionArguments = positionCaptor().getAllValues(); // -----------------------<2>
        Assertions.assertEquals(position, positionArguments.get(0)); // ------------------------ <3>
        var lastPosition = positionArguments.stream().reduce((p1, p2) -> { // ------------------ <4>
            Assertions.assertTrue(p2 >= p1);
            return p2;
        });
        Assertions.assertTrue(lastPosition.isPresent()); // ------------------------------------ <5>
        Assertions.assertTrue(lastPosition.get() < (position + HelloWorld.BYTES)); // ---------- <6>
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue()); // ----------------- <7>
        Assertions.assertSame(channel, result);
    }
}
