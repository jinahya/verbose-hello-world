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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.LongAdder;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#writeAsync(WritableByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_WriteWritableByteChannelTest extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeAsync(WritableByteChannel)} method throws a {@link NullPointerException} when {@code
     * channel} argument is {@code null}.
     */
    @DisplayName("write(channel) throws NullPointerException when channel is null")
    @Test
    void writeChannel_NullPointerException_ChannelIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.writeAsync((WritableByteChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(WritableByteChannel)} method invokes {@link HelloWorld#put(ByteBuffer)} method
     * with a byte buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the buffer to specified
     * channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) invokes put(buffer) and writes the buffer to the channel")
    @Test
    void writeChannel_InvokePutBufferWriteBufferToChannel_() throws IOException {
        final WritableByteChannel channel = mock(WritableByteChannel.class); // <1>
        final LongAdder writtenSoFar = new LongAdder();                      // <2>
        when(channel.write(any(ByteBuffer.class))).thenAnswer(i -> {         // <3>
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            final int written = current().nextInt(0, buffer.remaining() + 1);
            writtenSoFar.add(written);
            buffer.position(buffer.position() + written);
            return written;
        });
    }
}
