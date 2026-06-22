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
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#send(DatagramChannel, SocketAddress) send(channel, target)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.send(DatagramChannel, target)")
@Slf4j
class HelloWorld_Send_DatagramChannel_Target_Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        final var target = mock(SocketAddress.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.send(channel, target));
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code target}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / target is null")
    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(DatagramChannel.class);
        final var target = (SocketAddress) null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.send(channel, target));
    }

    /**
     * Verifies that the method delegates to {@code send(socket, target)} when the {@code channel}
     * is in blocking mode.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("blocking / delegates to send(socket, target)")
    @Test
    void __ChannelIsBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).<DatagramSocket>send(any(), any());
        final var channel = mock(DatagramChannel.class);
        doReturn(true).when(channel).isBlocking();
        final var socket = mock(DatagramSocket.class);
        doReturn(socket).when(channel).socket();
        doReturn(channel).when(socket).getChannel();
        final var target = mock(SocketAddress.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).send(socket, target);
        assertSame(channel, result);
    }

    /**
     * Verifies that the method sends {@code hello-world-bytes} via the {@code channel} to the
     * {@code target} when the {@code channel} is in non-blocking mode.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("non-blocking")
    @Test
    void __ChannelIsNotBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var channel = mock(DatagramChannel.class);
        doReturn(false).when(channel).isBlocking();
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src.capacity() == HelloWorld.BYTES;
            assert src.position() == 0;
            if (ThreadLocalRandom.current().nextInt() % 3 == 1) {
                return 0;
            }
            src.position(src.limit());
            return src.capacity();
        }).when(channel).send(notNull(), notNull());
        final var target = mock(SocketAddress.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service);
        verify(channel, atLeastOnce()).send(buffer, target);
        assertSame(channel, result);
    }
}
