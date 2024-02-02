package com.github.jinahya.hello.misc.c03calc;

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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcUdp3Client extends CalcUdp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            for (int i = 0; i < REQUEST_COUNT; i++) {
                // ---------------------------------------------------------------------------- open
                @SuppressWarnings({"java:S2095"})
                final var client = DatagramChannel.open();
                // ------------------------------------------------------------------ bind(optional)
                if (ThreadLocalRandom.current().nextBoolean()) {
                    try {
                        client.bind(new InetSocketAddress(HOST, 0));
                    } catch (final IOException ioe) {
                        log.error("failed to bind", ioe);
                    }
                }
                // --------------------------------------------------------------- connect(optional)
                if (ThreadLocalRandom.current().nextBoolean()) {
                    try {
                        client.connect(ADDR);
                    } catch (final IOException ioe) {
                        log.error("failed to connect", ioe);
                    }
                }
                // ------------------------------------------------- configure-non-blocking/register
                final var clientKey = client.configureBlocking(false).register(
                        selector,             // <sel>
                        SelectionKey.OP_WRITE // <ops>
                );
                assert !clientKey.isWritable();
            }
            // ----------------------------------------------------------------------------- prepare
            final var sequence = new AtomicInteger();
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select(TimeUnit.SECONDS.toMillis(1L)) == 0) {
                    log.error("failed to select in a while");
                    break;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // ------------------------------------------------------------------------ send
                    if (key.isWritable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message = new CalcMessage.OfBuffer()
                                .randomize()
                                .sequence(sequence);
                        if (channel.isConnected()) {
                            message.sendToServer(channel);
                        } else {
                            message.sendToServer(channel, ADDR);
                        }
                        key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        assert key.isWritable();
                        key.attach(message);
                        key.interestOpsOr(SelectionKey.OP_READ);
                        assert !key.isReadable();
                    }
                    // ----------------------------------------------------------------- receive/log
                    if (key.isReadable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message = (CalcMessage.OfBuffer) key.attachment();
                        message.receiveFromServer(channel).log();
                        if (channel.isConnected()) {
                            channel.disconnect();
                        }
                        channel.close();
                        assert !key.isValid();
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcUdp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
