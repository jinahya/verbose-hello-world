package com.github.jinahya.hello.miscellaneous.c01rfc863;

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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp1Client {

    public static void main(String... args) throws Exception {
        try (var client = new DatagramSocket(null)) {
            HelloWorldNetUtils.printSocketOptions(client);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR, 0));
                log.info("(optionally) bound to {}", client.getLocalSocketAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                try {
                    client.connect(_Rfc863Constants.ADDRESS);
                    log.info("(optionally) connected to {}, through {}",
                             client.getRemoteSocketAddress(), client.getLocalSocketAddress());
                } catch (SocketException se) {
                    log.warn("failed to connect", se);
                    connect = false;
                }
            }
            var array = new byte[
                    ThreadLocalRandom.current().nextInt(client.getSendBufferSize() + 1)
                    ];
            _Rfc863Utils.logClientBytes(array.length);
            ThreadLocalRandom.current().nextBytes(array);
            var packet = new DatagramPacket(array, array.length, _Rfc863Constants.ADDRESS);
            client.send(packet); // IOException
            var digest = _Rfc863Utils.newDigest();
            digest.update(array, 0, packet.getLength());
            _Rfc863Utils.logDigest(digest);
            if (connect) {
                client.disconnect(); // UncheckedIOException
            }
        }
    }

    private Rfc863Udp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
