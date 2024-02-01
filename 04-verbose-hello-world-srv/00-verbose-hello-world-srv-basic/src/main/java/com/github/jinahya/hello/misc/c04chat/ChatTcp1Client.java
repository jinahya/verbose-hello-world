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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp1Client extends _ChatTcp {

    private record Receiver(Socket client) implements Runnable {

        private Receiver {
            Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    array = client.getInputStream()
                            .readNBytes(_Message.BYTES);
                } catch (IOException ioe) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                if (array.length != _Message.BYTES) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                _Message.OfArray.printToSystemOut(array);
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
            }
        }
    }

    private record Sender(Socket client) implements Runnable {

        private Sender {
            Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void run() {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            var current = Thread.currentThread();
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        current.interrupt();
                        return null;
                    },
                    l -> {                         // <consumer>
                        if (!queue.offer(l)) {
                            log.error("failed to offer");
                        }
                    }
            );
            while (!Thread.currentThread().isInterrupted()) {
                String line;
                try {
                    if ((line = queue.poll(1L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                var array = _Message.OfArray.of(
                        _ChatUtils.prependUsername(line));
                try {
                    client.getOutputStream().write(array);
                    client.getOutputStream().flush();
                } catch (IOException ioe) {
                    if (!client.isClosed()) {
                        log.error("failed to send", ioe);
                    }
                    Thread.currentThread().interrupt();
                }
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
            }
        }
    }

    public static void main(final String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (final ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        try (final var client = new Socket()) {
            // --------------------------------------------------------------------------------- connect
            client.connect(
                    new InetSocketAddress(addr, PORT),
                    (int) CONNECT_TIMEOUT_MILLIS
            );
            log.debug("connected");
            // ----------------------------------------------------------------------------- prepare
            final var latch = new CountDownLatch(1);
            // ------------------------------------------------------------------ read-from-server/print
            Thread.ofPlatform().daemon().start(() -> {
                while (!client.isClosed()) {
                    try {
                        ___Message2.read(client.getInputStream()).print(System.out);
                    } catch (final IOException ioe) {
                        if (ioe instanceof EOFException) {
                            latch.countDown();
                            break;
                        }
                        if (!client.isClosed()) {
                            log.error("failed to read", ioe);
                        }
                    }
                }
            });
            // --------------------------------------------------------------- read-line/write-to-server
            JavaLangUtils.readLinesAndRunWhenTests(
                    "quit!"::equalsIgnoreCase,
                    latch::countDown,
                    l -> {
                        try {
                            ___Message2.write(client.getOutputStream(),
                                              ___Message2.prependUserName(l));
                        } catch (final Exception e) {
                            if (e instanceof IOException && client.isClosed()) {
                                return;
                            }
                            log.error("failed to write", e);
                        }
                    }
            );
            latch.await();
        }
    }
}
