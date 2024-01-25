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

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp1Server extends _CalcTcp {

    public static void main(final String... args) throws IOException, InterruptedException {
        try (var executor = Executors.newCachedThreadPool(
                Thread.ofVirtual().name("server-", 0L).factory());
             var server = new ServerSocket()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR);
            logBound(server);
            // -------------------------------------------------------------------------------- run
            JavaLangUtils.readLinesAndCloseWhenTests("quit!"::equalsIgnoreCase, server);
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
                executor.submit(() -> {
                    try {
                        client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
                        final var message = new byte[__CalcMessage2.BYTES];
                        final int r = client.getInputStream().readNBytes(
                                message,
                                0,
                                __CalcMessage2.INDEX_RESULT
                        );
                        if (r != __CalcMessage2.INDEX_RESULT) {
                            throw new EOFException("premature eof");
                        }
                        __CalcMessage2.calculateResult(message);
                        client.getOutputStream().write(message);
                        client.getOutputStream().flush();
                        return null;
                    } finally {
                        client.close();
                    }
                });
            }
            executor.shutdown();
            if (!executor.awaitTermination(1L, TimeUnit.SECONDS)) {
                log.error("executor not terminated!");
            }
        }
    }

    private CalcTcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
