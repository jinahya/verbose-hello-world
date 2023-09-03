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

@Slf4j
class Rfc862Udp1Server {

    public static void main(String... args) throws Exception {
        try (var server = new DatagramSocket(null)) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) _Rfc862Constants.ACCEPT_TIMEOUT_IN_MILLIS);
            var array = new byte[server.getReceiveBufferSize()];
            var packet = new DatagramPacket(array, array.length);
            server.receive(packet);
            server.send(packet);
            _Rfc862Utils.logServerBytes(packet.getLength());
            _Rfc862Utils.logDigest(array, 0, packet.getLength());
        }
    }

    private Rfc862Udp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
