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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
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

    @BeforeEach
    void _beforeEach() {
        _stubPutBufferToReturnTheBufferAsItsPositionIncreasedBy12();
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(channel) -> channel.write(put(buffer[12]))+")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(WritableByteChannel.class);               // <1>
        var writtenSoFar = new LongAdder();                          // <2>
        when(channel.write(argThat(b -> b != null && b.hasRemaining()))).thenAnswer(i -> { // <3>
            var src = i.getArgument(0, ByteBuffer.class);
            var written = current().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            return written;
        });
        // ------------------------------------------------------------------------------------ WHEN
        var result = service.write(channel);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Verify, service.put(buffer[12]) invoked, once.
        // TODO: Verify, channel.write(buffer) invoked, at least once.
        // TODO: Assert, writtenSoFar.intValue() is equal to BYTES.
        assertEquals(channel, result);
    }
}
