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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({
        "java:S2245"
})
@Slf4j
class Rfc862Udp2Client extends Rfc862Udp {

    public static void main(final String... args) throws Exception {
        // ------------------------------------------------------------------------------------ open
        try (var client = DatagramChannel.open()) {
            assert client.isBlocking(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ------------------------------------------------------------------- connect(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logConnected(client.connect(ADDR));
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(
                    (client.getOption(StandardSocketOptions.SO_SNDBUF) >> 1) + 1
            ));
            ThreadLocalRandom.current().nextBytes(buffer.array());
            logClientBytes(buffer.remaining());
            // -------------------------------------------------------------------------------- send
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var packet = new DatagramPacket(
                        buffer.array(),                           // <buf>
                        buffer.arrayOffset() + buffer.position(), // <offset>
                        buffer.remaining()                        // <length>
                );
                if (!client.isConnected()) {
                    packet.setSocketAddress(ADDR);
                }
                client.socket().send(packet);
                buffer.position(buffer.position() + packet.getLength());
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, packet.getLength());
            } else {
                if (client.isConnected()) {
                    final var w = client.write(buffer);
                    assert w == buffer.position();
                    assert !buffer.hasRemaining(); // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, buffer.position());
                } else {
                    final var w = client.send(buffer, ADDR);
                    assert w == buffer.position();
                    assert !buffer.hasRemaining(); // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, buffer.position());
                }
            }
            // ----------------------------------------------------------------------------- receive
            buffer.clear(); // position -> zero, limit -> capacity
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var packet = new DatagramPacket(
                        buffer.array(),                           // <buf>
                        buffer.arrayOffset() + buffer.position(), // <offset>
                        buffer.remaining()                        // <length>
                );
                client.socket().receive(packet);
                assert packet.getLength() == buffer.remaining();
                assert Objects.equals(packet.getSocketAddress(), ADDR);
                buffer.position(buffer.remaining());
            } else {
                final var source = client.receive(buffer);
                assert !buffer.hasRemaining();
                assert Objects.equals(source, ADDR);
            }
            // -------------------------------------------------------------------------- disconnect
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }

    private Rfc862Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
