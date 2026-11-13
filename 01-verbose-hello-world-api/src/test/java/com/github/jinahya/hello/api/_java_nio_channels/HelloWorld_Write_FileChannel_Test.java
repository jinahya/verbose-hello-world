package com.github.jinahya.hello.api._java_nio_channels;


/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
 * A class for testing {@link HelloWorld#write(FileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.write(FileChannel, position)")
@Slf4j
class HelloWorld_Write_FileChannel_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(FileChannel, long) write(channel, position)} method
     * throws a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("throws NPE / channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (FileChannel) null;
        final var position = ThreadLocalRandom.current().nextLong() >>> 1;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(channel, position));
    }

    /**
     * Verifies that the {@link HelloWorld#write(FileChannel, long) write(channel, position)} method
     * throws an {@link IllegalArgumentException} when the {@code position} argument is negative.
     */
    @DisplayName("throws IAE / position is negative")
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(FileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(IllegalArgumentException.class, () -> service.write(channel, position));
    }

    /**
     * Verifies that the {@link HelloWorld#write(FileChannel, long) write(channel, position)} method
     * writes all {@value HelloWorld#BYTES} bytes to the {@code channel}, across one or more partial
     * writes, each at the {@code position} adjusted by the previous writes, without touching the
     * {@code channel}'s own position, and returns the {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var channel = mock(FileChannel.class);
        final var bufferPositions = new ArrayList<Integer>();
        final var channelPositions = new ArrayList<Long>();
        doAnswer(w -> {
            final var src = w.getArgument(0, ByteBuffer.class);
            bufferPositions.add(src.position());
            channelPositions.add(w.getArgument(1, Long.class));
            final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
            src.position(src.position() + n);
            return n;
        }).when(channel).write(any(ByteBuffer.class), anyLong());
        final var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service());
        verify(channel, atLeastOnce()).write(same(buffer), anyLong());
        verifyNoMoreInteractions(channel);
        assertFalse(buffer.hasRemaining());
        assertEquals(0, bufferPositions.getFirst());
        for (var i = 0; i < channelPositions.size(); i++) {
            assertEquals(position + bufferPositions.get(i), channelPositions.get(i));
        }
        assertSame(channel, result);
    }

    /**
     * Verifies that the {@link HelloWorld#write(FileChannel, long) write(channel, position)} method
     * propagates an {@link IOException} thrown by the {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("throws IOException / channel fails")
    @Test
    void _ThrowIOException_ChannelFails() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var channel = mock(FileChannel.class);
        final var cause = new IOException("simulated write failure");
        doThrow(cause).when(channel).write(any(ByteBuffer.class), anyLong());
        // ------------------------------------------------------------------------------- when/then
        final var thrown = assertThrows(IOException.class, () -> service.write(channel, 0L));
        assertSame(cause, thrown);
    }
}
