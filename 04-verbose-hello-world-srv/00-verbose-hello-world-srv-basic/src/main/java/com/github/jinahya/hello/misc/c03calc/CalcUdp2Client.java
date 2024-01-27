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

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class CalcUdp2Client extends CalcUdp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            for (var c = 0; c < CLIENT_COUNT; c++) {
                var client = DatagramChannel.open(); // not-using-try-with-resources
                // ---------------------------------------------------------- configure-non-blocking
                client.configureBlocking(false);
                // ------------------------------------------------------------------------ register
                final var clientKey = client.register(
                        selector,
                        SelectionKey.OP_WRITE,
                        new _Message.OfBuffer().randomize()
                );
            }
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ----------------------------------------------------------- process-selected-keys
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // ------------------------------------------------------------------------ send
                    if (key.isWritable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message = (_Message.OfBuffer) key.attachment();
                        message.sendToServer(channel, ADDR);
                        assert !message.hasRemaining();
                        key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        assert key.isWritable(); // why?
                        key.interestOpsOr(SelectionKey.OP_READ);
                        assert !key.isReadable(); // why?
                    }
                    // --------------------------------------------------------------------- receive
                    if (key.isReadable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message = (_Message.OfBuffer) key.attachment();
                        message.receiveFromServer(channel).log();
                        key.interestOpsAnd(~SelectionKey.OP_READ);
                        assert key.isReadable(); // why?
                        channel.close();
                        assert !key.isValid();
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcUdp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
