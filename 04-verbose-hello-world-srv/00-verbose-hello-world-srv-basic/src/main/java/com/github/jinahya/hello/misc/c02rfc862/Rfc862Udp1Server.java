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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
class Rfc862Udp1Server extends Rfc862Udp {

    public static void main(final String... args) throws Exception {
        // ---------------------------------------------------------------------------------- create
        try (var server = new DatagramSocket(null)) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR);
            logBound(server);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var array = new byte[server.getReceiveBufferSize()];
            final var packet = new DatagramPacket(array, array.length);
            // ----------------------------------------------------------------------------- receive
            server.receive(packet);
            // -------------------------------------------------------------------------------- send
            server.send(packet);
            digest.update(packet.getData(), packet.getOffset(), packet.getLength());
            // --------------------------------------------------------------------------------- log
            logServerBytes(packet.getLength());
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Udp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
