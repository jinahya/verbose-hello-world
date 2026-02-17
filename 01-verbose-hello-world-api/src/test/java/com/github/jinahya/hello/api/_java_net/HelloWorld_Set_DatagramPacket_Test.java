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
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#set(DatagramPacket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(DatagramSocket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Set_DatagramPacket_Test
        extends HelloWorldTest {

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
        final var packet = (DatagramPacket) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.set(packet)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#set(DatagramPacket)} method, when
     * {@link DatagramPacket#getData() packet.data}'s length is greater than or equal to the
     * {@value HelloWorld#BYTES}, invokes {@link HelloWorld#set(byte[])}} with
     * {@link DatagramPacket#getData() packet.data}, and set with
     * {@link DatagramPacket#getData() packet.data}, {@code 0}, and {@value HelloWorld#BYTES}.
     */
    @DisplayName("""
            should invoke <set(packet.data)>,
            and should invoke <packet.set(packet.data, 0, 12)>""")
    @Test
    void __PacketDataLengthGreaterThanOrEqualTo12() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_will_return_the_array(service());
        final var packet = Mockito.mock(DatagramPacket.class);
        final var data = new byte[
                ThreadLocalRandom.current().nextInt(HelloWorld.BYTES) + HelloWorld.BYTES];
        Mockito.doReturn(data).when(packet).getData();
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(packet);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).set(data);
        Mockito.verify(packet, Mockito.times(1)).setData(data, 0, HelloWorld.BYTES);
        Assertions.assertSame(packet, result);
    }

    /**
     * Verifies that the {@link HelloWorld#set(DatagramPacket)} method, when
     * {@link DatagramPacket#getData() packet.data}'s length is less than the
     * {@value HelloWorld#BYTES}, invokes {@link HelloWorld#set(byte[])}} with an array of
     * {@value HelloWorld#BYTES}, and invokes {@link DatagramPacket#setData(byte[])} with the
     * array.
     */
    @DisplayName("""
            should invoke <set(byte[12])>,
            and should invoke <packet.set(array)>""")
    @Test
    void __PacketDataLengthIsLessThan12() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_will_return_the_array(service());
        final var packet = Mockito.mock(DatagramPacket.class);
        final var data = new byte[ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)];
        Mockito.doReturn(data).when(packet).getData();
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(packet);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(packet, Mockito.times(1)).setData(array);
        Assertions.assertSame(packet, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException, InterruptedException {
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
                for (int i = 0; i < 2; i++) {
                    try {
                        server.receive(packet);
                        assert packet.getLength() == HelloWorld.BYTES;
                        final var decoded = new String(
                                packet.getData(),
                                0,
                                packet.getLength(),
                                StandardCharsets.US_ASCII
                        );
                        log.debug("received: {}", decoded);
                    } catch (final IOException ioe) {
                        log.error("failed to receive", ioe);
                    }
                }
            });
            try (var client = new DatagramSocket()) {
                {
                    final var packet = new DatagramPacket(
                            new byte[HelloWorld.BYTES],
                            HelloWorld.BYTES,
                            server.getLocalSocketAddress()
                    );
                    service.set(packet);
                    client.send(packet);
                }
                {
                    final var packet = new DatagramPacket(
                            new byte[0],
                            0,
                            server.getLocalSocketAddress()
                    );
                    service.set(packet);
                    client.send(packet);
                }
            }
            thread.join();
        }
    }
}
