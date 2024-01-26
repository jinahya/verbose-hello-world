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

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class CalcUdp2Client extends _CalcUdp{

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            for (var i = 0; i < _CalcConstants.TOTAL_REQUESTS; i++) {
                // ---------------------------------------------------------------------------- open
                try (var client = DatagramChannel.open()) {
                    // ------------------------------------------------------ configure-non-blocking
                    client.configureBlocking(false);
                    // -------------------------------------------------------------------- register
                    final var clientKey = client.register(
                            selector,
                            SelectionKey.OP_WRITE,
                            new __CalcMessage3.OfBuffer().randomize()
                    );
                    // --------------------------------------------------------------- selector-loop
                    while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                        if (selector.select() == 0) {
                            continue;
                        }
                        for (final var j = selector.selectedKeys().iterator(); j.hasNext(); ) {
                            final var key = j.next();
                            j.remove();
                            // --------------------------------------------------------------- write
                            if (key.isWritable()) {
                                final var channel = (DatagramChannel) key.channel();
                                final var message = (__CalcMessage3.OfBuffer) key.attachment();
                                message.sendToServer(channel, ADDR);
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                                key.interestOpsOr(SelectionKey.OP_READ);
                            }
                            if (key.isReadable()) {
                                final var channel = (DatagramChannel) key.channel();
                                final var message = (__CalcMessage3.OfBuffer) key.attachment();
                                message.receiveFromServer(channel).log();
                                key.interestOpsAnd(~SelectionKey.OP_READ);
                                key.cancel();
                                assert !key.isValid();
                            }
                        }
                    }
                }
            }
        }
    }

    private CalcUdp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
