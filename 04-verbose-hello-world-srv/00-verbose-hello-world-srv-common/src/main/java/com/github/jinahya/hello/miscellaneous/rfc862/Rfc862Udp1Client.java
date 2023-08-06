package com.github.jinahya.hello.miscellaneous.rfc862;

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
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Rfc862Udp1Client {

    private static final int PACKET_LENGTH = ThreadLocalRandom.current()
            .nextInt(Rfc862Udp1Server.MAX_PACKET_LENGTH);

    public static void main(String... args) throws IOException {
        try (var client = new DatagramSocket(null)) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalSocketAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc862Constants.ENDPOINT);
                log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                          client.getLocalSocketAddress());
            }
            var digest = _Rfc862Utils.newMessageDigest();
            var buffer = new byte[PACKET_LENGTH];
            ThreadLocalRandom.current().nextBytes(buffer);
            var packet = new DatagramPacket(buffer, buffer.length, _Rfc862Constants.ENDPOINT);
            client.send(packet);
            log.debug("[C] {} byte(s) sent to {}, through {}", packet.getLength(),
                      packet.getSocketAddress(), client.getLocalSocketAddress());
            client.receive(packet);
            log.debug("[C] {} byte(s) received from {}, through {}", packet.getLength(),
                      packet.getSocketAddress(), client.getLocalSocketAddress());
            digest.update(buffer, 0, packet.getLength());
            if (connect) {
                client.disconnect();
            }
            log.debug("[S] digest: {}", Base64.getEncoder().encodeToString(digest.digest()));
        }
    }

    private Rfc862Udp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
