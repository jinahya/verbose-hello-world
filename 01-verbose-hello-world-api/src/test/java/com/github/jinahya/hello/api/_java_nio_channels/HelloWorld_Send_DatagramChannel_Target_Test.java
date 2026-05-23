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
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.atomic.*;

/**
 * A class for testing {@link HelloWorld#send(DatagramChannel, SocketAddress) send(channel, target)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Send_DatagramChannel_Target_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("(null, ?)NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        final var target = Mockito.mock(SocketAddress.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(channel, target)
        );
    }

    @DisplayName("(?, null)NullPointerException")
    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(DatagramChannel.class);
        final var target = (SocketAddress) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(channel, target)
        );
    }

    @Test
    void __ChannelIsBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(DatagramChannel.class);
        Mockito.when(channel.isBlocking()).thenReturn(true);
        final var socket = Mockito.mock(DatagramSocket.class);
        Mockito.when(channel.socket()).thenReturn(socket);
        Mockito.when(socket.getChannel()).thenReturn(channel);
        final var target = Mockito.mock(SocketAddress.class);
        Mockito.doReturn(socket).when(service).send(
                ArgumentMatchers.<DatagramSocket>same(socket),
                ArgumentMatchers.same(target)
        );
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).send(
                ArgumentMatchers.<DatagramSocket>same(socket),
                ArgumentMatchers.same(target)
        );
        Assertions.assertSame(channel, result);
    }

    @Test
    void __ChannelIsNotBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var srcRef = new AtomicReference<byte[]>();
        HelloWorldTestUtils.put_buffer_will_put_12_random_bytes(service, srcRef::set);
        final var channel = Mockito.mock(DatagramChannel.class);
        Mockito.when(channel.isBlocking()).thenReturn(false);
        final var target = Mockito.mock(SocketAddress.class);
        final var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
        Mockito.when(channel.send(
                ArgumentMatchers.argThat(v -> v != null && v.remaining() == HelloWorld.BYTES),
                ArgumentMatchers.same(target)
        )).thenAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var dst = new byte[src.remaining()];
            src.get(dst);
            baos.write(dst);
            return dst.length;
        });
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
//        Assertions.assertArrayEquals(srcRef.get(), baos.toByteArray());
        Assertions.assertSame(channel, result);
    }
}
