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
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({
        "java:S2245"
})
@Slf4j
class Rfc862Udp3Client extends Rfc862Udp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ------------------------------------------------------------------- connect(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logConnected(client.connect(ADDR));
            }
            // ------------------------------------------------------------------ configure/register
            final var clientKey = client.configureBlocking(false).register(
                    selector,
                    SelectionKey.OP_WRITE
            );
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(
                    (client.getOption(StandardSocketOptions.SO_SNDBUF) >> 1) + 1
            ));
            ThreadLocalRandom.current().nextBytes(buffer.array());
            logClientBytes(buffer.remaining());
            // ------------------------------------------------------------------------- select/send
            {
                // -------------------------------------------------------------------------- select
                final int k = selector.select();
                assert k == 1;
                // ------------------------------------------------------------------------- process
                final var selectedKey = selector.selectedKeys().iterator().next();
                assert selectedKey == clientKey;
                assert selectedKey.isWritable();
                final var channel = (DatagramChannel) selectedKey.channel();
                assert channel == client;
                // ---------------------------------------------------------------------------- send
                if (client.isConnected()) {
                    channel.write(buffer);
                } else {
                    channel.send(buffer, ADDR);
                }
                assert !buffer.hasRemaining(); // why?
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, buffer.position());
                selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                buffer.clear();
                selectedKey.interestOpsOr(SelectionKey.OP_READ);
                assert !selectedKey.isReadable();
            }
            // ---------------------------------------------------------------------- select/receive
            {
                // -------------------------------------------------------------------------- select
                final int k = selector.select();
                assert k == 1;
                // ------------------------------------------------------------------------- process
                final var selectedKey = selector.selectedKeys().iterator().next();
                assert selectedKey == clientKey;
                assert selectedKey.isReadable();
                final var channel = (DatagramChannel) selectedKey.channel();
                assert channel == client;
                // ------------------------------------------------------------------------- receive
                if (client.isConnected()) {
                    final int r = channel.read(buffer);
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    assert r == buffer.position();
                } else {
                    final var source = channel.receive(buffer);
                    assert Objects.equals(source, ADDR);
                }
                assert !buffer.hasRemaining();
                selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                // -------------------------------------------------------------------------- cancel
                selectedKey.cancel();
                assert !selectedKey.isValid();
            }
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
            // -------------------------------------------------------------------------- disconnect
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Udp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
