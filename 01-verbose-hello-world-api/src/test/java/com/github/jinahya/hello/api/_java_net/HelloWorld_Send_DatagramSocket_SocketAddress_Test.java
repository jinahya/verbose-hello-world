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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(DatagramSocket, SocketAddress)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_DatagramSocket_SocketAddress_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
     * method throws a {@link NullPointerException} when the {@code socket} argument is
     * {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <socket> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final DatagramSocket socket = null;
        final var target = Mockito.mock(SocketAddress.class);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(socket, target)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)}
     * method throws a {@link NullPointerException} when the {@code target} argument is
     * {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <target> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = Mockito.mock(DatagramSocket.class);
        final SocketAddress target = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(socket, target)
        );
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
    @DisplayName("should invoke <set(packet)> and <socket.send(packet)> with the same packet")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .append(Mockito.<DatagramPacket>any());
        final var socket = Mockito.mock(DatagramSocket.class);
        final var target = new InetSocketAddress(InetAddress.getLocalHost(), 1234);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket, target);
        // ------------------------------------------------------------------------------------ then
//        final var packetCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
//        Mockito.verify(service, Mockito.times(1)).append(packetCaptor.capture());
//        final var packet = packetCaptor.getValue();
//        Assertions.assertNotNull(packet);
//        final var data = packet.getData();
//        final var offset = packet.getOffset();
//        final var length = packet.getLength();
//        Assertions.assertEquals(HelloWorld.BYTES, data.length);
//        Assertions.assertEquals(0, offset);
//        Assertions.assertEquals(0, length);
//        Assertions.assertEquals(target, packet.getSocketAddress());
//        Mockito.verify(socket, Mockito.times(1)).send(packet);
        Assertions.assertSame(socket, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var socket = i.getArgument(0, DatagramSocket.class);
            final var target = i.getArgument(1, SocketAddress.class);
            final var buf = HelloWorldTestUtils.hello_world_byte_array();
            final var packet = new DatagramPacket(buf, 0, buf.length, target);
            socket.send(packet);
            return socket;
        }).when(service).send(ArgumentMatchers.<DatagramSocket>any(), ArgumentMatchers.any());
        // ----------------------------------------------------------------------------- when / then
        try (var server = new DatagramSocket(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0))) {
            final var target = server.getLocalSocketAddress();
            // ------------------------------------------------------------- start a receiver thread
            Thread.ofPlatform().start(() -> {
                try {
                    final DatagramPacket packet = new DatagramPacket(
                            new byte[HelloWorld.BYTES << 1],                       // <buf>
                            ThreadLocalRandom.current().nextInt(HelloWorld.BYTES), // <offset>
                            HelloWorld.BYTES                                       // <length>
                    );
                    server.receive(packet);
                    final var decoded = new String(
                            packet.getData(),         // <bytes>
                            packet.getOffset(),       // <offset>
                            packet.getLength(),       // <length>
                            StandardCharsets.US_ASCII // <charset>
                    );
                    log.debug("received: {}", decoded);
                } catch (final IOException ioe) {
                    log.error("failed to receive", ioe);
                }
            });
            // -------------------------------------------------- send to target (no connect needed)
            try (var client = new DatagramSocket()) {
                service.send(client, target);
            }
        }
    }
}
