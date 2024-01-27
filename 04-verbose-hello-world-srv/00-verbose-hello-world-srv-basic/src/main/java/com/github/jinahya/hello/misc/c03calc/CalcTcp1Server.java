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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp1Server extends CalcTcp {

    public static void main(final String... args) throws IOException, InterruptedException {
        try (var executor = newExecutorForServer("tcp1-server-");
             var server = new ServerSocket()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, SERVER_BACKLOG);
            logBound(server);
            // --------------------------------------------------------- read-quit!-and-close-server
            JavaLangUtils.readLinesAndCloseWhenTests(
                    "quit!"::equalsIgnoreCase,
                    server
            );
            // -------------------------------------------------------------------------------- loop
            while (!server.isClosed()) {
                final Socket client;
                try {
                    client = server.accept();
                } catch (final IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to accept", ioe);
                    }
                    continue;
                }
                try {
                    executor.submit(() -> {
                        try {
                            try {
                                client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
                                // -------------------------------------------- read/calculate/write
                                new _Message.OfArray()
                                        .readFromClient(client.getInputStream())
                                        .calculateResult()
                                        .writeToClient(client.getOutputStream(), true);
                            } finally {
                                client.close();
                            }
                        } catch (final Exception e) {
                            log.error("failed to serve for " + client, e);
                        }
                    });
                } catch (final RejectedExecutionException ree) {
                    if (!executor.isShutdown()) {
                        log.error("failed to submit task", ree);
                    }
                }
            }
            // ---------------------------------------------------------------------- shutdown/await
            executor.shutdown();
            if (!executor.awaitTermination(4L, TimeUnit.SECONDS)) {
                log.error("executor not terminated!");
            }
        }
    }

    private CalcTcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
