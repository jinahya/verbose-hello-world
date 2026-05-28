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

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
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
@Slf4j
class HelloWorld_Send_DatagramChannel_Target_Test extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("(null, ?)NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        final var target = mock(SocketAddress.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.send(channel, target));
    }

    @DisplayName("(?, null)NullPointerException")
    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(DatagramChannel.class);
        final var target = (SocketAddress) null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.send(channel, target));
    }

    @Test
    void __ChannelIsBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).<DatagramSocket>send(any(), any());
        final var channel = mock(DatagramChannel.class);
        when(channel.isBlocking()).thenReturn(true);
        final var socket = mock(DatagramSocket.class);
        when(channel.socket()).thenReturn(socket);
        when(socket.getChannel()).thenReturn(channel);
        final var target = mock(SocketAddress.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).send(socket, target);
        assertSame(channel, result);
    }

    @Test
    void __ChannelIsNotBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var channel = mock(DatagramChannel.class);
        when(channel.isBlocking()).thenReturn(false);
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src != null;
            assert src.capacity() == HelloWorld.BYTES;
            assert src.remaining() == HelloWorld.BYTES;
            if (ThreadLocalRandom.current().nextBoolean()) {
                return 0;
            }
            src.position(HelloWorld.BYTES);
            return HelloWorld.BYTES;
        }).when(channel).send(any(), any());
        final var target = mock(SocketAddress.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service);
        verify(channel, atLeastOnce()).send(buffer, target);
        assertSame(channel, result);
    }
}
