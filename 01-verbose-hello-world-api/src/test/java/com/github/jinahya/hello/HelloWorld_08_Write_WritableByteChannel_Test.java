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

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_08_Write_WritableByteChannel_Arguments_Test
 */
@DisplayName("write(WritableByteChannel)")
@Slf4j
class HelloWorld_08_Write_WritableByteChannel_Test extends HelloWorldTest {

    @BeforeEach
    void stub_PutBuffer_FillBuffer() {
        var service = service();
        lenient()
                .doAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    assert buffer.remaining() >= HelloWorld.BYTES;
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(service)
                .put(notNull());
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes and writes the buffer to specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> put(buffer[12])"
                 + " -> channel.write(buffer)+")
    @Test
    void _InvokePutBufferWriteBufferToChannel_() throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: WritableByteChannel
        var channel = mock(WritableByteChannel.class);                       // <1>
        var writtenSoFar = new LongAdder();                                  // <2>
        lenient().
                when(channel.write(notNull())).thenAnswer(i -> {             // <3>
                    var buffer = i.getArgument(0, ByteBuffer.class);         // <4>
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
        var service = service();
        var channel = mock(WritableByteChannel.class);
        lenient()
                .when(channel.write(notNull()))
                .thenAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
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
