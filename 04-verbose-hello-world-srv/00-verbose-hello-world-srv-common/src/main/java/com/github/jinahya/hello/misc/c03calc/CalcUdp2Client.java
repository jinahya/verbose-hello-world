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
class CalcUdp2Client {

    public static void main(final String... args)
            throws Exception {
        try (var selector = Selector.open()) {
            for (var c = 0; c < _CalcConstants.TOTAL_REQUESTS; c++) {
                try (var client = DatagramChannel.open()) {
                    client.configureBlocking(false);
                    final var clientKey = client.register(
                            selector,
                            SelectionKey.OP_WRITE,
                            _CalcMessage.newInstanceForClient()
                    );
                    while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                        if (selector.select(_CalcConstants.SELECT_TIMEOUT) == 0) {
                            continue;
                        }
                        for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                            final var selectedKey = i.next();
                            i.remove();
                            if (selectedKey.isWritable()) {
                                final var channel = (DatagramChannel) selectedKey.channel();
                                final var attachment = (_CalcMessage) selectedKey.attachment();
                                attachment.sendRequest(channel);
                                selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                                attachment.readyToReceiveResult();
                                selectedKey.interestOpsOr(SelectionKey.OP_READ);
                            }
                            if (selectedKey.isReadable()) {
                                final var channel = (DatagramChannel) selectedKey.channel();
                                final var attachment = (_CalcMessage) selectedKey.attachment();
                                attachment.receiveResult(channel).log();
                                selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                                selectedKey.cancel();
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
