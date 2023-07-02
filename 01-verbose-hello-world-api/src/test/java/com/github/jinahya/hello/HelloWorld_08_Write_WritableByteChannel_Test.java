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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_08_Write_WritableByteChannel_Arguments_Test
 */
@DisplayName("write(channel)")
@Slf4j
class HelloWorld_08_Write_WritableByteChannel_Test extends _HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer) put(buffer)} method to return the {@code buffer} as
     * its {@link ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     */
    @BeforeEach
    void stub_ReturnBufferAsItsPositionIncreasedBy12_PutBuffer() {
        var service = serviceInstance();
        doAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            assert buffer.capacity() == BYTES;
            assert buffer.position() == 0;
            assert buffer.remaining() == BYTES;
            buffer.position(BYTES);
            return buffer;
        }).when(service).put(any());
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("put(buffer[12])"
                 + " -> channel.write(buffer)+")
    @Test
    void _InvokePutBufferWriteBufferToChannel_() throws IOException {
        // GIVEN
        var service = serviceInstance();
        var channel = mock(WritableByteChannel.class);               // <1>
        var writtenSoFar = new LongAdder();                          // <2>
        when(channel.write(any())).thenAnswer(i -> {                 // <3>
            ByteBuffer buffer = i.getArgument(0);                    // <4>
            assert buffer != null;
            assert buffer.hasRemaining();
            var written = current().nextInt(buffer.remaining() + 1); // <5>
            buffer.position(buffer.position() + written);            // <6>
            writtenSoFar.add(written);                               // <7>
            return written;                                          // <8>
        });
        // WHEN
        service.write(channel);
        // THEN: once, put(buffer[12]) invoked
        // THEN: at least once, channel.write(buffer) invoked
        // THEN: 12 bytes are written to the channel
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method returns the
     * {@code channel} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns channel")
    @Test
    void _ReturnChannel_() throws IOException {
        // GIVEN
        var service = serviceInstance();
        var channel = mock(WritableByteChannel.class);
        when(channel.write(any())).thenAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            assert buffer.hasRemaining();
            var written = buffer.remaining();
            buffer.position(buffer.limit());
            return written;
        });
        // WHEN
        var actual = service.write(channel);
        // THEN
        assertSame(channel, actual);
    }
}
