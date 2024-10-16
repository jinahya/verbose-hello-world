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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp2Server extends Rfc863Udp {

    public static void main(final String... args) throws Exception {
        try (var server = DatagramChannel.open()) {
            assert server.isBlocking(); // !!!
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR);
            logBound(server);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var buffer = ByteBuffer.allocate(
                    server.getOption(StandardSocketOptions.SO_RCVBUF)
            );
            // ----------------------------------------------------------------------------- receive
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var packet = new DatagramPacket(
                        buffer.array(),                           // <buf>
                        buffer.arrayOffset() + buffer.position(), // <offset>
                        buffer.remaining()                        // <length>
                );
                server.socket().receive(packet);
                assert packet.getSocketAddress() != null;
                buffer.position(buffer.position() + packet.getLength());
            } else {
                final var address = server.receive(buffer);
            }
            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, buffer.position());
            // --------------------------------------------------------------------------------- log
            logServerBytes(buffer.position());
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
