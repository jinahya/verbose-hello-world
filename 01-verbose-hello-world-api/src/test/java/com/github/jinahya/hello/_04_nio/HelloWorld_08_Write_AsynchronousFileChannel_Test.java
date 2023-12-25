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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
 * @see HelloWorld_08_Write_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_08_Write_AsynchronousFileChannel_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        putBuffer_WillReturnTheBuffer_AsItsPositionIncreasedBy12();
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
        var service = service();
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        _stub_ToWriteSome(channel, writtenSoFar);
        var position = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE - HelloWorld.BYTES);
        // ------------------------------------------------------------------------------------ when
        var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor().capture()); // ------------ <1>
        var buffer = bufferCaptor().getValue();
        Mockito.verify(channel, Mockito.atLeastOnce())
                .write(ArgumentMatchers.same(buffer), positionCaptor().capture()); // ---------- <2>
        var positionArguments = positionCaptor().getAllValues(); // -----------------------------<3>
        Assertions.assertEquals(position, positionArguments.get(0)); // ------------------------ <4>
        var lastPosition = positionArguments.stream().reduce((p1, p2) -> { // ------------------ <5>
            Assertions.assertTrue(p2 >= p1);
            return p2;
        });
        Assertions.assertTrue(lastPosition.isPresent());
        Assertions.assertTrue(lastPosition.get() < position + HelloWorld.BYTES);
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue()); // ----------------- <6>
        Assertions.assertSame(channel, result);
    }
}
