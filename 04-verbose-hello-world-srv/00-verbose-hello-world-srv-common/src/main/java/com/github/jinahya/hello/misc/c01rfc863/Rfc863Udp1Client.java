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

import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._UdpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp1Client {

    public static void main(final String... args)
            throws Exception {
        try (var client = new DatagramSocket(null)) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR.getAddress(), 0));
                _UdpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            final var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc863Constants.ADDR);
                _UdpUtils.logConnected(client);
            }
            // -------------------------------------------------------------------------------- send
            final var array = new byte[
                    ThreadLocalRandom.current().nextInt(client.getSendBufferSize() + 1)
                    ];
            ThreadLocalRandom.current().nextBytes(array);
            _Rfc863Utils.logClientBytes(array.length);
            final var packet = new DatagramPacket(array, array.length, _Rfc863Constants.ADDR);
            assert packet.getLength() == array.length;
            client.send(packet);
            _Rfc863Utils.logDigest(array, 0, packet.getLength());
            // -------------------------------------------------------------------------- disconnect
            if (connect) {
                client.disconnect();
            }
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
