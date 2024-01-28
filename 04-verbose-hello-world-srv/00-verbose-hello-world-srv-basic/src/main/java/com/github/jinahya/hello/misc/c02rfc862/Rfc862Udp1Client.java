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

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Udp1Client extends Rfc862Udp {

    public static void main(final String... args) throws Exception {
        // ---------------------------------------------------------------------------------- create
        try (var client = new DatagramSocket(null)) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ------------------------------------------------------------------- connect(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.connect(ADDR);
                logConnected(client);
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var array = new byte[
                    ThreadLocalRandom.current().nextInt((client.getSendBufferSize() >> 1) + 1)
                    ];
            ThreadLocalRandom.current().nextBytes(array);
            final var packet = new DatagramPacket(array, array.length, ADDR);
            assert packet.getData() == array;
            assert packet.getOffset() == 0;
            assert packet.getLength() == array.length;
            if (!client.isConnected()) {
                packet.setSocketAddress(ADDR);
            }
            logClientBytes(packet.getLength());
            // -------------------------------------------------------------------------------- send
            client.send(packet);
            digest.update(packet.getData(), packet.getOffset(), packet.getLength());
            // ----------------------------------------------------------------------------- receive
            client.receive(packet);
            assert packet.getLength() == array.length;
            assert Objects.equals(packet.getSocketAddress(), ADDR);
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
            // -------------------------------------------------------------------------- disconnect
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }

    private Rfc862Udp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
