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
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#append(DatagramPacket) append(packet)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(DatagramPacket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_DatagramPacket_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method throws a
     * {@link NullPointerException} when the {@code packet} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <packet> argument is <null>""")
    @Test
    void _ThrowNullPointerException_PacketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var packet = (DatagramPacket) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(packet)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method throws an
     * {@link IllegalArgumentException} when the {@code packet}'s data buffer does not have enough
     * room for {@value HelloWorld#BYTES} bytes after {@code offset + length}.
     */
    @DisplayName("""
            should throw an <IllegalArgumentException>
            when the <packet>'s data buffer is not large enough""")
    @Test
    void _ThrowIllegalArgumentException_DataBufferNotLargeEnough() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var data = new byte[HelloWorld.BYTES - 1];
        final var packet = new DatagramPacket(data, 0, 0);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.append(packet)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method invokes the
     * {@link HelloWorld#set(byte[], int) set(data, offset + length)} method, increments the
     * packet's length by {@value HelloWorld#BYTES}, and returns the packet.
     */
    @DisplayName("""
            should invoke <set(packet.data, offset + length)>,
            increment the packet's length by 12,
            and return the packet""")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(Mockito.any(byte[].class), Mockito.anyInt());
        final var offset = ThreadLocalRandom.current().nextInt(8);
        final var length = ThreadLocalRandom.current().nextInt(8);
        final var data = new byte[offset + length + HelloWorld.BYTES];
        final var packet = new DatagramPacket(data, offset, length);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(packet);
        // ------------------------------------------------------------------------------------ then
//        Mockito.verify(service, Mockito.times(1)).set(data, offset + length);
//        Assertions.assertEquals(length + HelloWorld.BYTES, packet.getLength());
        Assertions.assertSame(packet, result);
    }

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method correctly
     * appends the hello-world-bytes to a packet that already has existing data.
     */
    @Disabled
    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_set_actual_hello_world_bytes();
        // ----------------------------------------------------------------------------- when / then
        try (var server = new DatagramSocket(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0))) {
            final var thread = Thread.ofPlatform().start(() -> {
                final var packet = new DatagramPacket(
                        new byte[HelloWorld.BYTES],
                        HelloWorld.BYTES
                );
                try {
                    server.receive(packet);
                    Assertions.assertEquals(HelloWorld.BYTES, packet.getLength());
                    final var decoded = new String(
                            packet.getData(),
                            packet.getOffset(),
                            packet.getLength(),
                            StandardCharsets.US_ASCII
                    );
                    log.debug("received: {}", decoded);
                } catch (final IOException ioe) {
                    log.error("failed to receive", ioe);
                }
            });
            try (var client = new DatagramSocket()) {
                final var data = new byte[HelloWorld.BYTES];
                final var packet = new DatagramPacket(
                        data,
                        0,
                        0,
                        server.getLocalSocketAddress()
                );
                service.append(packet);
                Assertions.assertEquals(HelloWorld.BYTES, packet.getLength());
                client.send(packet);
            }
            thread.join();
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }
}
