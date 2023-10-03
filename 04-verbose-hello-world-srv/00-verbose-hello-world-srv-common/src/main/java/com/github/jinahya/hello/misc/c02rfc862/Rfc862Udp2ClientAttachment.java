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

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Udp2ClientAttachment {

    Rfc862Udp2ClientAttachment(final DatagramChannel channel) throws IOException {
        super();
        this.client = Objects.requireNonNull(channel, "channel is null");
        buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(
                channel.getOption(StandardSocketOptions.SO_SNDBUF) + 1
        ));
    }

    int send() throws IOException {
        _Rfc862Utils.logClientBytes(buffer.remaining());
        final int s = client.send(buffer, _Rfc862Constants.ADDR);
        assert s == buffer.position();
        assert !buffer.hasRemaining();
        _Rfc862Utils.logDigest(buffer.flip());
        return s;
    }

    SocketAddress receive() throws IOException {
        buffer.clear();
        final var address = client.receive(buffer);
        assert !buffer.hasRemaining();
        log.debug("{} byte(s) received from {}", buffer.position(), address);
        return address;
    }

    private final DatagramChannel client;

    private final ByteBuffer buffer;
}
