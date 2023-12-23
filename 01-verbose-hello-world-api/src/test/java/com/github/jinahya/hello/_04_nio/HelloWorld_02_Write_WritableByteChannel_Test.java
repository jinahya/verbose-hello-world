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
import com.github.jinahya.hello._HelloWorldTestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.LongAdder;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.Channels.newChannel;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Write_WritableByteChannel_Arguments_Test
 */
@DisplayName("write(channel)")
@Slf4j
class HelloWorld_02_Write_WritableByteChannel_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> put(buffer[12]) -> channel.write(buffer)+")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(WritableByteChannel.class);                       // <1>
        final var writtenSoFar = new LongAdder();                                  // <2>
        _HelloWorldTestHelper.stub_WriteBuffer_ToWriteSome(channel, writtenSoFar); // <3>
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).put(bufferCaptor().capture()); // <1>
        final var buffer = bufferCaptor().getValue();            // <2>
        assertNotNull(buffer);                                   // <3>
        assertEquals(HelloWorld.BYTES, buffer.capacity());       // <4>
        // TODO: Verify, channel.write(buffer) invoked, at least once.
        // TODO: Assert, buffer has no remaining.
        // TODO: Assert, writtenSoFar.intValue() is equal to HelloWorld.BYTES.
        assertSame(channel, result);
    }

    @org.junit.jupiter.api.Disabled("not implemented yet")
    // TODO: remove when implemented
    @Test
    void _ReadByteArrayInputStream_WriteByteArrayOutputStream()
            throws IOException {
        var service = service();
        var output = new ByteArrayOutputStream(HelloWorld.BYTES);
        var writable = service.write(newChannel(output));
        service.write(writable);
        var input = new ByteArrayInputStream(output.toByteArray());
        var readable = newChannel(input);
        var buffer = allocate(HelloWorld.BYTES);
        while (buffer.hasRemaining()) {
            readable.read(buffer);
        }
    }

    @org.junit.jupiter.api.Disabled("not implemented yet")
    // TODO: remove when implemented
    @Test
    void _ReadPipeSource_WritePipeSink()
            throws IOException, InterruptedException {
        var service = service();
        var pipe = Pipe.open();
        var thread = new Thread(() -> {
            try {
                service.write(pipe.sink());
            } catch (final IOException ioe) {
                throw new UncheckedIOException("failed to write", ioe);
            }
        });
        thread.start();
        for (var buffer = ByteBuffer.allocate(HelloWorld.BYTES); buffer.hasRemaining(); ) {
            pipe.source().read(buffer);
        }
        thread.join(SECONDS.toMillis(1L));
    }
}
