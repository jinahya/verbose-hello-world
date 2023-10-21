package com.github.jinahya.hello.misc.c02rfc862;

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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Udp1Client {

    public static void main(final String... args) throws Exception {
        try (var client = new DatagramSocket(null)) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalSocketAddress());
            }
            final var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc862Constants.ADDR);
                log.info("(optionally) connected to {}, through {}",
                         client.getRemoteSocketAddress(), client.getLocalSocketAddress());
            }
            final var length = ThreadLocalRandom.current().nextInt(client.getSendBufferSize());
            final var array = new byte[length];
            ThreadLocalRandom.current().nextBytes(array);
            final var packet = new DatagramPacket(array, array.length, _Rfc862Constants.ADDR);
            _Rfc862Utils.logClientBytes(packet.getLength());
            client.send(packet);
            _Rfc862Utils.logDigest(array, 0, packet.getLength());
            client.setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_IN_MILLIS);
            client.receive(packet);
            log.debug("{} byte(s) received from {}", packet.getLength(), packet.getSocketAddress());
            if (connect) {
                client.disconnect();
            }
        }
    }

    private Rfc862Udp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}