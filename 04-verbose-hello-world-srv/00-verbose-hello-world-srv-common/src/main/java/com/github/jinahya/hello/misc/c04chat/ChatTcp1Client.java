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
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp1Client {

    private record Receiver(Socket client)
            implements Runnable {

        private Receiver {
            Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    array = client.getInputStream()
                            .readNBytes(_ChatMessage.BYTES);
                } catch (IOException ioe) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                if (array.length != _ChatMessage.BYTES) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                _ChatMessage.OfArray.printToSystemOut(array);
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
            }
        }
    }

    private record Sender(Socket client)
            implements Runnable {

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
                var array = _ChatMessage.OfArray.of(
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

    public static void main(String... args)
            throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        var executor = Executors.newFixedThreadPool(2);
        try (var client = new Socket()) {
            client.connect(new InetSocketAddress(addr, _ChatConstants.PORT));
            _TcpUtils.logConnected(client);
            executor.submit(new Sender(client));
            executor.submit(new Receiver(client));
            for (executor.shutdown();
                 !executor.awaitTermination(8L, TimeUnit.SECONDS); ) {
                // empty
            }
        }
    }
}
