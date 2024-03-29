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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._UdpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp2Server {

    public static void main(final String... args)
            throws Exception {
        try (var server = DatagramChannel.open()) {
            assert server.isBlocking();
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc863Constants.ADDR);
            _UdpUtils.logBound(server);
            // ----------------------------------------------------------------------------- receive
            final var buffer = ByteBuffer.allocate(
                    server.getOption(StandardSocketOptions.SO_RCVBUF)
            );
            assert buffer.hasArray();
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var packet = new DatagramPacket(
                        buffer.array(),                           // <buf>
                        buffer.arrayOffset() + buffer.position(), // <offset>
                        buffer.remaining()                        // <length>
                );
                server.socket().receive(packet);
                _Rfc863Utils.logServerBytes(packet);
                buffer.position(buffer.position() + packet.getLength());
            } else {
                final var address = server.receive(buffer);
                _Rfc863Utils.logServerBytes(buffer, address);
            }
            _Rfc863Utils.logDigest(buffer.flip());
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
