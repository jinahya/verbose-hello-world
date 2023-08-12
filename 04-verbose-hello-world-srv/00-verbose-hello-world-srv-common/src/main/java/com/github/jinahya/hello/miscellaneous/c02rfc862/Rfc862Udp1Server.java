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
import java.util.Base64;
import java.util.concurrent.TimeUnit;

// https://www.rfc-editor.org/rfc/rfc862
@Slf4j
public class Rfc862Udp1Server {

    static final int MAX_PACKET_LENGTH = 1024;

    public static void main(String... args) throws Exception {
        try (var server = new DatagramSocket(null)) {
            server.setReuseAddress(true);
            server.bind(_Rfc862Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            var digest = _Rfc862Utils.newMessageDigest();
            var buffer = new byte[MAX_PACKET_LENGTH];
            var packet = new DatagramPacket(buffer, buffer.length);
            server.receive(packet);
            log.debug("[S] {} byte(s) received from {}", packet.getLength(),
                      packet.getSocketAddress());
            digest.update(buffer, 0, packet.getLength());
            server.send(packet);
            log.debug("[S] echoed back to {}", packet.getSocketAddress());
            log.debug("[S] digest: {}", Base64.getEncoder().encodeToString(digest.digest()));
        }
    }

    private Rfc862Udp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
