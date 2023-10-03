package com.github.jinahya.hello.misc.c01rfc863;

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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Z_Rfc863Tcp1Server {

    public static void main(final String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc863Constants.ADDR, Z__Rfc863Constants.SERVER_BACKLOG);
            log.info("bound to {}", server.getLocalSocketAddress());
            JavaLangUtils.readLinesAndCloseWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    server,                        // <closeable>
                    null                           // <consumer>
            );
//            final var executor = Executors.newCachedThreadPool();
            final var executor = Executors.newFixedThreadPool(Z__Rfc863Constants.SERVER_THREADS);
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
                    try (client) {
                        final var array = new byte[Z__Rfc863Constants.SERVER_BUFLEN];
                        while (client.getInputStream().read(array) == -1) {
                            // do nothing
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                        throw ioe;
                    }
                    return null;
                });
            }
            executor.shutdown();
            log.debug("awaiting executor to be terminated...");
            if (!executor.awaitTermination(10L, TimeUnit.MINUTES)) {
                log.error("executor hasn't been terminated for a while");
            }
        }
    }

    /**
     * Creates a new instance.
     */
    private Z_Rfc863Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
