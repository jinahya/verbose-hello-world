package com.github.jinahya.hello.miscellaneous.rfc863;

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
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
public class Rfc863Udp1Server {

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    static final int PORT = Rfc863Tcp1Server.PORT;

    static final int MAX_PACKET_LENGTH = 8;

    public static void log(DatagramPacket packet) {
        log.debug("[S] {} byte(s) received from {}", packet.getLength(),
                  packet.getSocketAddress());
    }

    public static void main(String... args) throws IOException {
        try (var server = new DatagramSocket(null)) {
            {
                var endpoint = new InetSocketAddress(HOST, PORT);
                server.bind(endpoint);
                log.info("[S] server bound to {}", server.getLocalSocketAddress());
            }
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            var buffer = new byte[MAX_PACKET_LENGTH];
            var packet = new DatagramPacket(buffer, buffer.length);
            server.receive(packet);
            log.debug("[S] {} byte(s) received from {}", packet.getLength(),
                      packet.getSocketAddress());
        }
    }

    private Rfc863Udp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
