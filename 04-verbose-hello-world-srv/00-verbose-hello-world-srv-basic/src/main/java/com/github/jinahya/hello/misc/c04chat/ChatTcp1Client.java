package com.github.jinahya.hello.misc.c04chat;

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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

@Slf4j
class ChatTcp1Client extends ChatTcp {

    public static void main(final String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (final ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        try (final var client = new Socket()) {
            // ----------------------------------------------------------------------------- connect
            client.connect(
                    new InetSocketAddress(addr, PORT),
                    (int) CONNECT_TIMEOUT_MILLIS
            );
            // ----------------------------------------------------------------------------- prepare
            final var latch = new CountDownLatch(1);
            // -------------------------------------------------------------- read-from-server/print
            Thread.ofPlatform().daemon().start(() -> {
                final var message = new ChatMessage.OfArray();
                while (!client.isClosed()) {
                    try {
                        message.read(client.getInputStream()).print();
                    } catch (final IOException ioe) {
                        if (!client.isClosed()) {
                            log.error("failed to read", ioe);
                        }
                        break;
                    }
                }
            });
            // ----------------------------------------- read-quit!/count-down-latch|write-to-server
            final var message = new ChatMessage.OfArray();
            JavaLangUtils.readLinesAndRunWhenTests(
                    "quit!"::equalsIgnoreCase,
                    latch::countDown,
                    l -> {
                        if (l.isBlank()) {
                            return;
                        }
                        try {
                            message.message(ChatMessage.prependUserName(l))
                                    .write(client.getOutputStream())
                                    .flush();
                        } catch (final IOException ioe) {
                            if (!client.isClosed()) {
                                log.error("failed to write", ioe);
                            }
                        }
                    }
            );
            // ------------------------------------------------------------------------- await-latch
            latch.await();
        }
    }
}
