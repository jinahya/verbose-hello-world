package com.github.jinahya.hello.misc.c01rfc863;

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
import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
class Rfc863Udp1Server {

    public static void main(final String... args) throws Exception {
        try (var server = new DatagramSocket(null)) {
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) _Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS);
            // -------------------------------------------------------------------------------------
            final var array = new byte[server.getReceiveBufferSize()];
            final var packet = new DatagramPacket(array, array.length);
            server.receive(packet);
            log.debug("{} byte(s) received from {}", packet.getLength(), packet.getSocketAddress());
            assert packet.getLength() <= array.length;
            // -------------------------------------------------------------------------------------
            _Rfc863Utils.logServerBytes(packet.getLength());
            _Rfc863Utils.logDigest(packet.getData(), 0, packet.getLength());
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
