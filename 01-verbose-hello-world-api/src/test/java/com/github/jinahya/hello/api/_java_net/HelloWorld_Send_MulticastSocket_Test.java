package com.github.jinahya.hello.api._java_net;

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
import java.nio.charset.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

@Disabled
@Slf4j
class HelloWorld_Send_MulticastSocket_Test
        extends HelloWorldTest {

    // "232.1.1.1"   source-specific multicast, SSM (RFC 4607)
    // "224.1.1.1"   transient, global scope (not well-known)
    // "239.0.0.1"   administratively scoped, local scope (RFC 2365)
    // "239.192.1.1" administratively scoped, organization-local (RFC 2365)
    // "239.255.1.1" administratively scoped, site-local (RFC 2365)
    private static final String MULTICAST_HOST4 = "239.255.1.1";

    // "ff02::1"       link-local, all-nodes (well-known, avoid joining)
    // "ff02::1:2:3"   link-local, custom group (RFC 4291)
    // "ff05::1"       site-local scope (RFC 4291)
    // "ff08::1"       organization-local scope (RFC 4291)
    // "ff0e::1"       global scope (RFC 4291)
    private static final String MULTICAST_HOST6 = "ff02::1:2:3";

    private static final int MULTICAST_PORT =
            ThreadLocalRandom.current().nextInt(49152, 65536);

    // ---------------------------------------------------------------------------------------------
    @DisplayName("IPv4")
    @Test
    void __IPv4() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var socket = i.getArgument(0, DatagramSocket.class);
            socket.send(new DatagramPacket(
                    HelloWorldTestUtils.hello_world_byte_array(),
                    HelloWorld.BYTES
            ));
            return socket;
        }).when(service).send(ArgumentMatchers.<DatagramSocket>any());
        final var mcastaddr = new InetSocketAddress(
                InetAddress.getByName(MULTICAST_HOST4),
                MULTICAST_PORT
        );
        final var netIf = NetworkInterface.networkInterfaces()
                .filter(ni -> {
                    try {
                        return ni.isUp()
                               && ni.supportsMulticast()
                               && !ni.isLoopback()
                               && ni.inetAddresses().anyMatch(a -> a instanceof Inet4Address);
                    } catch (final SocketException se) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
        Assumptions.assumeTrue(netIf != null, "no IPv4 multicast-capable non-loopback interface");
        log.debug("netIf: {} ({})", netIf.getName(), netIf.getDisplayName());
        final var count = ThreadLocalRandom.current().nextInt(2, 6);
        final var latch = new CountDownLatch(count);
        final var received = new AtomicInteger(0);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, count).forEach(i -> executor.execute(() -> {
                try (var receiver = new MulticastSocket(null)) {
                    receiver.setReuseAddress(true);
                    receiver.bind(new InetSocketAddress(
                            InetAddress.getByName("0.0.0.0"), MULTICAST_PORT));
                    receiver.joinGroup(mcastaddr, netIf);
                    receiver.setSoTimeout(1_000);
                    try {
                        latch.countDown();
                        final var packet = new DatagramPacket(
                                new byte[HelloWorld.BYTES],
                                HelloWorld.BYTES
                        );
                        receiver.receive(packet);
                        received.incrementAndGet();
                        final var message = new String(
                                packet.getData(),
                                0,
                                packet.getLength(),
                                StandardCharsets.US_ASCII
                        );
                        log.debug("[{}] received: {}", i, message);
                    } finally {
                        receiver.leaveGroup(mcastaddr, netIf);
                    }
                } catch (final IOException ioe) {
                    latch.countDown();
                    log.error("[{}] failed", i, ioe);
                }
            }));
            latch.await();
            try (var sender = new MulticastSocket(null)) {
                sender.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0));
                sender.setNetworkInterface(netIf);
                sender.connect(mcastaddr);
                service.send(sender);
            }
        }
        Assumptions.assumeTrue(
                received.get() == count,
                "only " + received.get() + "/" + count + " receivers got the packet"
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("IPv6")
    @Test
    void __IPv6() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var socket = i.getArgument(0, DatagramSocket.class);
            socket.send(new DatagramPacket(
                    HelloWorldTestUtils.hello_world_byte_array(),
                    HelloWorld.BYTES
            ));
            return socket;
        }).when(service).send(ArgumentMatchers.<DatagramSocket>any());
        final var mcastaddr = new InetSocketAddress(
                InetAddress.getByName(MULTICAST_HOST6),
                MULTICAST_PORT
        );
        final var netIf = NetworkInterface.networkInterfaces()
                .filter(ni -> {
                    try {
                        return ni.isUp()
                               && ni.supportsMulticast()
                               && !ni.isLoopback()
                               && ni.inetAddresses().anyMatch(
                                a -> a instanceof Inet6Address ia
                                     && ia.isLinkLocalAddress());
                    } catch (final SocketException se) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
        Assumptions.assumeTrue(netIf != null,
                               "no IPv6 link-local multicast-capable non-loopback interface");
        log.debug("netIf: {} ({})", netIf.getName(), netIf.getDisplayName());
        final var count = ThreadLocalRandom.current().nextInt(2, 6);
        final var latch = new CountDownLatch(count);
        final var received = new AtomicInteger(0);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, count).forEach(i -> executor.execute(() -> {
                try (var receiver = new MulticastSocket(null)) {
                    receiver.setReuseAddress(true);
                    receiver.bind(new InetSocketAddress(
                            InetAddress.getByName("::"), MULTICAST_PORT));
                    receiver.joinGroup(mcastaddr, netIf);
                    receiver.setSoTimeout(1_000);
                    try {
                        latch.countDown();
                        final var packet = new DatagramPacket(
                                new byte[HelloWorld.BYTES],
                                HelloWorld.BYTES
                        );
                        receiver.receive(packet);
                        received.incrementAndGet();
                        final var message = new String(
                                packet.getData(),
                                0,
                                packet.getLength(),
                                StandardCharsets.US_ASCII
                        );
                        log.debug("[{}] received: {}", i, message);
                    } finally {
                        receiver.leaveGroup(mcastaddr, netIf);
                    }
                } catch (final IOException ioe) {
                    latch.countDown();
                    log.error("[{}] failed", i, ioe);
                }
            }));
            latch.await();
            try (var sender = new MulticastSocket(null)) {
                sender.bind(new InetSocketAddress(InetAddress.getByName("::"), 0));
                sender.setNetworkInterface(netIf);
                try {
                    sender.connect(mcastaddr);
                } catch (final IOException ioe) {
                    Assumptions.assumeTrue(false, "sender cannot connect: " + ioe.getMessage());
                }
                service.send(sender);
            }
        }
        Assumptions.assumeTrue(
                received.get() == count,
                "only " + received.get() + "/" + count + " receivers got the packet"
        );
    }
}
