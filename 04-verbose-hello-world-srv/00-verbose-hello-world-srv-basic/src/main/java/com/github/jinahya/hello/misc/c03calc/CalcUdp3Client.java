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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcUdp3Client extends CalcUdp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            for (var c = 0; c < REQUEST_COUNT; c++) {
                @SuppressWarnings({"java:S2095"})
                var client = DatagramChannel.open(); // not-using-try-with-resources
                // ------------------------------------------------- configure-non-blocking/register
                final var clientKey = client.configureBlocking(false).register(
                        selector,
                        SelectionKey.OP_WRITE,
                        new _Message.OfBuffer().randomize()
                );
            }
            // ----------------------------------------------------------------------- selector-loop
            final var index = new AtomicInteger();
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    // ------------------------------------------------------------------------ send
                    if (selectedKey.isWritable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        message.sendToServer(channel, ADDR);
                        assert !message.hasRemaining();
                        selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        assert selectedKey.isWritable();
                        selectedKey.interestOpsOr(SelectionKey.OP_READ);
                        assert !selectedKey.isReadable();
                    }
                    // ----------------------------------------------------------------- receive/log
                    if (selectedKey.isReadable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        message.receiveFromServer(channel).log(index.getAndIncrement());
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        assert selectedKey.isReadable();
                        channel.close();
                        assert !selectedKey.isValid();
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
