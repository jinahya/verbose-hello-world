package com.github.jinahya.hello.misc.c01rfc863.real;

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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863TcpBlockingServer {

    public static void main(final String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalSocketAddress());
            JavaLangUtils.readLinesAndCloseWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    server,                        // <closeable>
                    null                           // <consumer>
            );
            final var service = Executors.newFixedThreadPool(_Rfc863Constants.SERVER_THREADS);
            while (!server.isClosed()) {
                final Socket client;
                try {
                    client = server.accept();
                } catch (final IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to accept", ioe);
                    }
                    break;
                }
                service.submit(() -> {
                    try (client) {
                        client.setSoTimeout((int) _Rfc863Constants.READ_TIMEOUT_MILLIS);
                        final var array = new byte[_Rfc863Constants.SERVER_BUFLEN];
                        while (!Thread.currentThread().isInterrupted()) {
                            if (client.getInputStream().read(array) == -1) {
                                break;
                            }
                        }
                    }
                    return null;
                });
            }
            service.shutdown();
            log.debug("awaiting service to be terminated...");
            if (!service.awaitTermination(10L, TimeUnit.MINUTES)) {
                log.error("service hasn't been terminated for a while");
            }
        }
    }

    private Rfc863TcpBlockingServer() {
        throw new AssertionError("instantiation is not allowed");
    }
}
