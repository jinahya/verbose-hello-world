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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcTcp3Client extends CalcTcp {

    private static void connect(final Selector selector) throws IOException {
//        log.debug("connecting...");
        final var client = SocketChannel.open();
        try {
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false);
            // ------------------------------------------------------------------------ connect(try)
            if (client.connect(ADDR)) {
                log.debug("connected immediately");
                final var clientKey = client.register(
                        selector,
                        SelectionKey.OP_WRITE,
                        new _Message.OfBuffer().randomize().readyToWriteToServer()
                );
                assert !clientKey.isWritable();
                connect(selector);
            } else {
                final var clientKey = client.register(
                        selector,
                        SelectionKey.OP_CONNECT
                );
                assert !clientKey.isConnectable();
            }
            log.debug("waking up...");
            selector.wakeup();
        } catch (final IOException ioe) {
            log.error("failed to configure-non-blocking/connect(try)", ioe);
            client.close();
        }
    }

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
//             final var executor = newExecutorForClient("tcp-3-client-")
        ) {
//            for (int i = 0; i < REQUEST_COUNT; i++) {
////                final Future<Void> submitter = executor.submit(() -> {
//                    connect(selector);
////                    return null;
////                });
////            submitter.get();
//            }
            final var index = new AtomicInteger();
            final var requests = new AtomicInteger(REQUEST_COUNT);
            // ------------------------------------------------------------------------ connect(try)
            if (requests.getAndDecrement() > 0) {
                connect(selector);
            }
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                log.debug("selecting among {}", selector.keys().size());
                if (selector.select() == 0) {
//                if (selector.select(TimeUnit.SECONDS.toMillis(1L)) == 0) {
                    continue;
                }
                log.debug("selectedKeys.size: {}", selector.selectedKeys().size());
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    log.debug("selectedKey: c: {}, w: {}, r: {}", selectedKey.isConnectable(), selectedKey.isWritable(), selectedKey.isReadable());
                    // ------------------------------------------------------------- connect(finish)
                    if (selectedKey.isConnectable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        try {
                            if (channel.finishConnect()) {
                                log.debug("connected");
                                selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                                selectedKey.attach(
                                        new _Message.OfBuffer()
                                                .randomize()
                                                .readyToWriteToServer()
                                );
                                selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                                assert !selectedKey.isWritable();
                            // -------------------------------------------------------- connect(try)
                                if (requests.getAndDecrement() > 0) {
                                    connect(selector);
                                }
                            }
                        } catch (final IOException ioe) {
                            log.error("failed to finish connecting", ioe);
                            channel.close();
                            assert !selectedKey.isValid();
                            continue;
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        assert message.hasRemaining();
                        final var w = message.write(channel);
                        log.debug("written {}", w);
                        assert w > 0; // why?
                        if (!message.hasRemaining()) {
                            log.debug("all written");
                            channel.shutdownOutput();
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            assert selectedKey.isWritable();
                            message.readyToReadFromServer();
                            selectedKey.interestOpsOr(SelectionKey.OP_READ);
                            assert !selectedKey.isReadable();
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        assert message.hasRemaining();
                        final var r = message.read(channel);
                        log.debug("read: {}", r);
                        if (r == -1) {
                            log.error("premature eof");
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                            assert selectedKey.isReadable();
                            channel.close();
                            assert !selectedKey.isValid();
                            continue;
                        }
                        assert r > 0; // why?
                        if (!message.hasRemaining()) {
                            log.debug("all read");
                            channel.shutdownInput();
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                            message.log(index.getAndIncrement());
                            log.debug("closing...");
                            channel.close();
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
//            // -----------------------------------------------------------------------------------------------------------------
//            log.debug("shutting down executor...");
//            executor.shutdown();
//            if (!executor.awaitTermination(1L, TimeUnit.SECONDS)) {
//                log.error("executor not terminated");
//            }
//            assert executor.isTerminated();
//            log.debug("executor terminated");
        }
        log.error("end-of-main");
    }
}
