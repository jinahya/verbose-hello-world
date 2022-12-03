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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_08_Write_WritableByteChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_08_Write_WritableByteChannel_Test extends HelloWorldTest {

    // TODO: Remove this stubbing method when you implemented the put(buffer) method!
    @BeforeEach
    void stub_PutBuffer_FillBuffer() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        Mockito.lenient()
                .doAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    assert buffer.remaining() >= HelloWorld.BYTES;
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(helloWorld())
                .put(ArgumentMatchers.notNull());
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the buffer to specified
     * channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer[12])"
                 + " and writes the buffer to the channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_() throws IOException {
        var channel = Mockito.mock(WritableByteChannel.class);    // <1>
        var writtenSoFar = new LongAdder();                       // <2>
        Mockito.lenient()
                .when(channel.write(ArgumentMatchers.notNull()))  // <3>
                .thenAnswer(i -> {
                    ByteBuffer buffer = i.getArgument(0);         // <4>
                    var written = ThreadLocalRandom.current()
                            .nextInt(buffer.remaining() + 1);     // <5>
                    buffer.position(buffer.position() + written); // <6>
                    writtenSoFar.add(written);                    // <7>
                    return written;                               // <8>
                });
        // TODO: Invoke helloWorld().write(channel)
        // TODO: Verify helloWorld() invoked, once, put(buffer[12])
        // TODO: Verify helloWorld() invoked, at least once, channel.write(buffer)
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method returns the
     * {@code channel} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) returns channel")
    @Test
    void write_ReturnChannel_() throws IOException {
        var channel = Mockito.mock(WritableByteChannel.class);
        Mockito.lenient()
                .when(channel.write(ArgumentMatchers.notNull()))
                .thenAnswer(i -> {
                    ByteBuffer buffer = i.getArgument(0);
                    var written = buffer.remaining();
                    buffer.position(buffer.limit());
                    return written;
                });
        var actual = helloWorld().write(channel);
        Assertions.assertSame(channel, actual);
    }
}
