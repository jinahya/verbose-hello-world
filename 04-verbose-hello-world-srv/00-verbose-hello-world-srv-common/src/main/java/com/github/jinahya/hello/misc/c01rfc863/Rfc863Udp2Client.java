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
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp2Client {

    public static void main(final String... args) throws Exception {
        try (var client = DatagramChannel.open()) {
            assert client.isBlocking();
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
            final var buffer = ByteBuffer.allocate(
                    ThreadLocalRandom.current().nextInt(
//                            client.getOption(StandardSocketOptions.SO_SNDBUF) + 1
                            (client.getOption(StandardSocketOptions.SO_SNDBUF) >> 1) + 1
                    )
            );
            ThreadLocalRandom.current().nextBytes(buffer.array());
            _Rfc863Utils.logClientBytes(buffer.remaining());
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var packet = new DatagramPacket(
                        buffer.array(),                           // <buf>
                        buffer.arrayOffset() + buffer.position(), // <offset>
                        buffer.remaining(),                       // <length>
                        _Rfc863Constants.ADDR                     // <address>
                );
                client.socket().send(packet);
                assert packet.getLength() == buffer.remaining();
                buffer.position(buffer.position() + packet.getLength());
            } else {
                if (connect && ThreadLocalRandom.current().nextBoolean()) {
                    final var w = client.write(buffer);
                    assert w == buffer.position();
                } else {
                    final var w = client.send(buffer, _Rfc863Constants.ADDR);
                    assert w == buffer.position();
                }
            }
            assert !buffer.hasRemaining();
            _Rfc863Utils.logDigest(buffer.flip());
            // -------------------------------------------------------------------------- disconnect
            if (connect) {
                client.disconnect();
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
