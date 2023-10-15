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

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp1Server {

    private static void serve(final ServerSocket server) throws InterruptedException {
        Objects.requireNonNull(server, "server is null");
        final var executor = Executors.newCachedThreadPool();
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
                    client.setSoTimeout(_CalcConstants.READ_TIMEOUT_MILLIS);
                    // -------------------------------------------------------------------- read
                    final var array = _CalcUtils.newArrayForServer();
                    final var length = array.length - Integer.BYTES;
                    final int r = client.getInputStream().readNBytes(array, 0, length);
                    if (r < length) {
                        throw new EOFException("unexpected eof");
                    }
                    // ------------------------------------------------------------------- apply
                    CalcOperator.apply(array);
                    // ------------------------------------------------------------------- write
                    client.getOutputStream().write(array, length, Integer.BYTES);
                    client.getOutputStream().flush();
                }
                return null;
            });
        }
        executor.shutdown();
        final var terminated = executor.awaitTermination(10L, TimeUnit.SECONDS);
        assert terminated;
    }

    public static void main(final String... args) throws IOException, InterruptedException {
        try (var server = new ServerSocket()) {
            // -------------------------------------------------------------------------------- BIND
            server.bind(_CalcConstants.ADDR, 50);
            _TcpUtils.logBound(server);
            JavaLangUtils.readLinesAndCloseWhenTests(HelloWorldServerUtils::isQuit, server);
            serve(server);
        }
    }

    private CalcTcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
