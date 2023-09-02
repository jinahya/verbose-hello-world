package com.github.jinahya.hello.miscellaneous.c02rfc862;

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
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp1Server {

    public static void main(String... args) throws Exception {
        try (var server = new DatagramSocket(null)) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(16L));
            var array = new byte[server.getReceiveBufferSize()];
            var packet = new DatagramPacket(array, array.length);
            server.receive(packet);
            log.info("received from {}", packet.getSocketAddress());
            var digest = _Rfc862Utils.newDigest();
            digest.update(array, 0, packet.getLength());
            _Rfc862Utils.logDigest(digest);
            server.send(packet);
            _Rfc862Utils.logServerBytesSent(packet.getLength());
        }
    }

    private Rfc862Udp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
