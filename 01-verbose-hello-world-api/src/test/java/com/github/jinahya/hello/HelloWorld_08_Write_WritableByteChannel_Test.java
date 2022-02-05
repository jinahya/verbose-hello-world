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
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

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
@Slf4j
class HelloWorld_08_Write_WritableByteChannel_Test
        extends HelloWorldTest {

    // TODO: Remove this stubbing method when you implemented the put(buffer) method!
    @BeforeEach
    void stub_PutBuffer_FillBuffer() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        lenient().doAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0);
            assert buffer.remaining() >= HelloWorld.BYTES;
            buffer.position(buffer.position() + HelloWorld.BYTES);
            return buffer;
        }).when(helloWorld()).put(notNull());
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with
     * a byte buffer of {@link HelloWorld#BYTES} bytes and writes the buffer to
     * specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer)"
                 + " and writes the buffer to the channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_() throws IOException {
        final WritableByteChannel channel = mock(WritableByteChannel.class);
        final LongAdder writtenSoFar = new LongAdder();           // <2>
        lenient().when(channel.write(notNull()))                  // <3>
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);   // <4>
                    final int written                             // <5>
                            = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written); // <6>
                    writtenSoFar.add(written);                    // <7>
                    return written;                               // <8>
                });
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)}
     * method returns the {@code channel} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) returns channel")
    @Test
    void write_ReturnChannel_() throws IOException {
        final WritableByteChannel channel = mock(WritableByteChannel.class);
        lenient().when(channel.write(notNull()))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final int written = buffer.remaining();
                    buffer.position(buffer.limit());
                    return written;
                });
        final WritableByteChannel actual = helloWorld().write(channel);
        assertSame(channel, actual);
    }
}