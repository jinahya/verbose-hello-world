package com.github.jinahya.hello.api._03_java_net;

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
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#send(DatagramSocket) send(socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(DatagramSocket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_DatagramSocket_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket) send(socket)} method throws a
     * {@link NullPointerException} when the {@code socket} argument is {@code null}.
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
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(socket)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket) send(socket)} method throws an
     * {@link IllegalArgumentException} when the {@code socket} is not connected.
     */
    @DisplayName("""
            should throw an <IllegalArgumentException>
            when the <socket> is not connected"""
    )
    @Test
    void _ThrowIllegalArgumentException_SocketIsNotConnected() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = Mockito.mock(DatagramSocket.class);
        Mockito.when(socket.isConnected()).thenReturn(false);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.send(socket)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket) send(socket)} method invokes
     * {@link HelloWorld#set(DatagramPacket) set(packet)} and
     * {@link DatagramSocket#send(DatagramPacket) socket.send(packet)} with the same packet.
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
                .set(Mockito.any(DatagramPacket.class));
        final var socket = Mockito.mock(DatagramSocket.class);
        Mockito.when(socket.isConnected()).thenReturn(true);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket);
        // ------------------------------------------------------------------------------------ then
        final var captor = ArgumentCaptor.forClass(DatagramPacket.class);
        Mockito.verify(service, Mockito.times(1)).set(captor.capture()); // <1>
        final var packet = captor.getValue();
        Mockito.verify(socket, Mockito.times(1)).send(packet);           // <2>
        Assertions.assertSame(socket, result);                           // <3>
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        stub_set_array_will_set_actual_hello_world_bytes();
        // ----------------------------------------------------------------------------- when / then
        try (var server = new DatagramSocket(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0))) {
            // start a receiver thread
            Thread.ofVirtual().start(() -> {
                try {
                    final var packet = new DatagramPacket(new byte[HelloWorld.BYTES],
                                                          HelloWorld.BYTES);
                    server.receive(packet);
                    log.debug("received: {}",
                              new String(packet.getData(), 0, packet.getLength(),
                                         StandardCharsets.US_ASCII));
                } catch (final IOException ioe) {
                    log.error("failed to receive", ioe);
                }
            });
            try (var client = new DatagramSocket()) {
                client.connect(server.getLocalSocketAddress());
                assert client.isConnected();
                final var result = service.send(client);
                Assertions.assertSame(client, result);
            }
        }
    }
}
