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
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class CalcUdp3Server extends CalcUdp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open();
             var executor = newExecutorForServer("udp-2-server-")) {
            // ----------------------------------------------------------------- SO_REUSE(ADDR|PORT)
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            try {
                server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            } catch (final UnsupportedOperationException uoe) {
                log.warn("not supported: {}", StandardSocketOptions.SO_REUSEPORT, uoe);
            }
            // ------------------------------------------------ bind/configure-non-blocking/register
            final var serverKey = server.bind(ADDR)
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
            // ----------------------------------------------------------------------------- prepare
            final var messages = new ArrayList<CalcMessage.OfBuffer>();
            final var lock = new ReentrantLock();
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // ---------------------------------------------------- receive/calculate/wakeup
                    if (key.isReadable()) {
                        final var channel = (DatagramChannel) key.channel();
                        final var message = new CalcMessage.OfBuffer().receiveFromClient(channel);
                        assert !message.hasRemaining();
                        message.calculate(executor, m -> {
                            m.readyToWriteToClient();
                            lock.lock();
                            try {
                                messages.add(m);
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                            } finally {
                                lock.unlock();
                            }
                            selector.wakeup(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        });
                    }
                    // ------------------------------------------------------------------------ send
                    if (key.isWritable()) {
                        final var channel = (DatagramChannel) key.channel();
                        lock.lock();
                        try {
                            assert !messages.isEmpty();
                            messages.removeFirst().sendToClient(channel);
                            if (messages.isEmpty()) {
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcUdp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
