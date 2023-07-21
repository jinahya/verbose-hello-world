package com.github.jinahya.hello.miscellaneous.m1_rfc863;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Rfc863UdpClient1 {

    private static final boolean BIND = false;

    private static final boolean CONNECT = false;

    public static void send(SocketAddress endpoint) throws IOException {
        try (var client = new DatagramSocket(null)) {
            if (BIND) {
                client.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                log.debug("[C] client bound to {}", client.getLocalSocketAddress());
            }
            if (CONNECT) {
                client.connect(endpoint);
                log.debug("[C] client connected");
                var other = new InetSocketAddress(InetAddress.getLocalHost(), 1234);
                try {
                    client.send(new DatagramPacket(new byte[0], 0, other));
                    assert false;
                } catch (IllegalArgumentException iae) {
                    log.debug("[C] unable to send to other than connected: {}",
                              iae.getMessage());
                }
            }
            var length = ThreadLocalRandom.current()
                    .nextInt(1, Rfc863UdpServer1.MAX_PACKET_LENGTH + 1);
            var buffer = new byte[length];
            var packet = new DatagramPacket(buffer, buffer.length, endpoint);
            client.send(packet);
            log.debug("[C] {} byte(s) sent to {}, through {}", packet.getLength(),
                      packet.getSocketAddress(), client.getLocalSocketAddress());
            if (CONNECT) {
                client.disconnect();
                log.debug("[C] client disconnected");
            }
        }
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc863UdpServer1.PORT);
        send(endpoint);
    }

    private Rfc863UdpClient1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
