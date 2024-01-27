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

import com.github.jinahya.hello.util.JavaLangUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcUdp2Server extends CalcUdp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open();
             var executor = newExecutorForServer("udp-2-server-")) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // ------------------------------------------------------------- bind/configure/register
            final var serverKey = logBound(server.bind(ADDR))
                    .configureBlocking(false)
                    .register(
                            selector,
                            SelectionKey.OP_READ
                    );
            // --------------------------------------------- read-quit!/close-server/wakeup-selector
            JavaLangUtils.readLinesAndCallWhenTests(
                    "quit!"::equalsIgnoreCase,
                    () -> {
                        server.close();
                        assert !serverKey.isValid();
                        selector.wakeup(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        return null;
                    }
            );
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ----------------------------------------------------------- process-selected-keys
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // --------------------------------------------------------------------- receive
                    if (key.isReadable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message =
                                new _Message.OfBuffer().receiveFromClient(channel);
                        key.interestOpsAnd(~SelectionKey.OP_READ);
                        message.calculateResult(executor, m -> {
                            key.attach(m);
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                            assert !serverKey.isWritable();
                            selector.wakeup();
                        });
                    }
                    // ------------------------------------------------------------------------ send
                    if (key.isWritable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message = (_Message.OfBuffer) key.attachment();
                        message.sendToClient(channel);
                        key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        assert key.isWritable();
                    }
                }
            }
            // ------------------------------------------------------------- shutdown/await-executor
            executor.shutdown();
            if (!executor.awaitTermination(1L, TimeUnit.SECONDS)) {
                log.error("executor not terminated");
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcUdp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
