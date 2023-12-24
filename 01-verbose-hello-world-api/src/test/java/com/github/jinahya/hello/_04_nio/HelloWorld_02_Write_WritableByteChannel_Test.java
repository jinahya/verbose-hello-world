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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_02_Write_WritableByteChannel_Arguments_Test
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_02_Write_WritableByteChannel_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        putBuffer_WillReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
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
        final var channel = Mockito.mock(WritableByteChannel.class);                   // <1>
        final var writtenSoFar = new LongAdder();                                      // <2>
        _HelloWorldTestUtils.writeBuffer_willWriteSome(channel, writtenSoFar);         // <3>
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor().capture()); // <1>
        final var buffer = bufferCaptor().getValue();                            // <2>
        Assertions.assertNotNull(buffer);                                        // <3>
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());            // <4>
        // TODO: verify, channel.write(buffer) invoked, at least once.
        // TODO: assert, buffer has no remaining.
        // TODO: assert, writtenSoFar.intValue() is equal to HelloWorld.BYTES.
        Assertions.assertSame(channel, result);
    }
}
