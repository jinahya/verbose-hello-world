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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp3Client extends _CalcTcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            try (var executor = Executors.newFixedThreadPool(
                    CLIENT_THREADS,
                    Thread.ofVirtual().name("calc-tcp-3-client-", 0L).factory())) {
                for (int i = 0; i < CLIENT_COUNT; i++) {
                    executor.submit(() -> {
                        var client = SocketChannel.open(); // no-try-with-resources
                        // ---------------------------------------------------------- bind(optional)
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            client.bind(new InetSocketAddress(HOST, 0));
                        }
                        // --------------------------------------------------------------- configure
                        client.configureBlocking(false);
                        // ------------------------------------------------------------ connect(try)
                        if (client.connect(ADDR)) {
                            return client.register(
                                    selector,
                                    SelectionKey.OP_WRITE,
                                    __CalcMessage2.newRandomizedBuffer()
                                            .limit(__CalcMessage2.INDEX_RESULT)
                                            .position(__CalcMessage2.INDEX_OPERATOR)
                            );
                        } else {
                            return client.register(selector, SelectionKey.OP_CONNECT);
                        }
                    }).get();
                }
                executor.shutdown();
                final var terminated = executor.awaitTermination(1L, TimeUnit.SECONDS);
                if (!terminated) {
                    log.error("executor not terminated");
                }
            }
            // ---------------------------------------------------------------------- selection-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select() == 0) {
                    continue;
                }
                // --------------------------------------------------------------- selectedKeys-loop
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
                    // ------------------------------------------------------------- connect(finish)
                    if (key.isConnectable()) {
                        final var channel = (SocketChannel) key.channel();
                        if (channel.finishConnect()) {
                            key.attach(
                                    __CalcMessage2.newRandomizedBuffer()
                                            .limit(__CalcMessage2.INDEX_RESULT)
                                            .position(__CalcMessage2.INDEX_OPERATOR)
                            );
                            key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                            assert !key.isWritable();
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var buffer = (ByteBuffer) key.attachment();
                        assert buffer.hasRemaining();
                        final var w = channel.write(buffer);
                        log.debug("w: {}", w);
                        assert w >= 0;
                        if (!buffer.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            assert buffer.position() == __CalcMessage2.INDEX_RESULT;
                            buffer.limit(buffer.capacity());
                            assert buffer.remaining() == 1;
                            key.interestOpsOr(SelectionKey.OP_READ);
                            assert !key.isReadable();
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var buffer = (ByteBuffer) key.attachment();
                        assert buffer.hasRemaining();
                        final var r = channel.read(buffer);
                        log.debug("r: {}", r);
                        if (r == -1) {
                            log.error("premature eof");
                            channel.close();
                            assert !key.isValid();
                            continue;
                        }
                        assert r >= 0;
                        if (!buffer.hasRemaining()) {
                            __CalcMessage2.log(buffer);
                            channel.close();
                            assert !key.isValid();
                        }
                    }
                }
            }
        }
    }
}
