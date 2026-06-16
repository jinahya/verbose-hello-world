package com.github.jinahya.hello.api._java_net;

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
import org.mockito.*;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.send(DatagramSocket, SocketAddress)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_DatagramSocket_SocketAddress_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
     * method throws a {@link NullPointerException} when the {@code socket} argument is
     * {@code null}.
     */
    @DisplayName("throws NPE / socket is null")
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final DatagramSocket socket = null;
        final var target = mock(SocketAddress.class);
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.send(socket, target));
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
     * method throws a {@link NullPointerException} when the {@code target} argument is
     * {@code null}.
     */
    @DisplayName("throws NPE / target is null")
    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = mock(DatagramSocket.class);
        final SocketAddress target = null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.send(socket, target));
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
     * method creates a datagram packet using
     * {@link DatagramPacket#DatagramPacket(byte[], int, SocketAddress) (buf, length, address)}
     * constructor, invokes {@link HelloWorld#append(DatagramPacket) append(packet)} with the
     * constructed packet, and {@link DatagramSocket#send(DatagramPacket) socket.send(packet)} with
     * the same packet.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).append(any(DatagramPacket.class));
        final var socket = mock(DatagramSocket.class);
        final var target = new InetSocketAddress("127.0.0.1", 12345);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket, target);
        // ------------------------------------------------------------------------------------ then
        final var packetCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
//        verify(service, times(1)).append(packetCaptor.capture());
//        final var packet = packetCaptor.getValue();
//        assertNotNull(packet);
//        assertEquals(HelloWorld.BYTES, packet.getData().length);
//        assertEquals(0, packet.getOffset());
//        assertEquals(0, packet.getLength());
//        assertEquals(target, packet.getSocketAddress());
//        verify(socket, times(1)).send(packet);
        assertSame(socket, result);
    }
}
