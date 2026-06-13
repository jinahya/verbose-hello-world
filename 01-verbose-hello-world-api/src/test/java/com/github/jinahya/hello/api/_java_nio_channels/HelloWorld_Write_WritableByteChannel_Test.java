package com.github.jinahya.hello.api._java_nio_channels;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_WritableByteChannel_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(WritableByteChannel) write(channel)} method throws
     * a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <channel> argument is <null>")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (WritableByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(channel));
    }

    /**
     * Verifies that the method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} with a
     * {@value HelloWorld#BYTES}-byte buffer and writes the buffer to the specified
     * {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <put(buffer[12])> and write the <buffer> to the <channel>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_put_random_bytes(service());
        final var channel = mock(WritableByteChannel.class);
        final var bufferPositions = new ArrayList<Integer>();
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src != null;
            assert src.limit() == HelloWorld.BYTES;
            assert src.hasRemaining();
            final var position = src.position();
            bufferPositions.add(position);
            final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
            src.position(position + n);
            return n;
        }).when(channel).write(any());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service);
//        verify(channel, atLeastOnce()).write(buffer);
//        verifyNoMoreInteractions(channel);
//        assertEquals(0, bufferPositions.getFirst());
//        for (var i = 1; i < bufferPositions.size(); i++) {
//            assertTrue(bufferPositions.get(i) > bufferPositions.get(i - 1));
//        }
//        assertFalse(buffer.hasRemaining());
        assertSame(channel, result);
    }
}
