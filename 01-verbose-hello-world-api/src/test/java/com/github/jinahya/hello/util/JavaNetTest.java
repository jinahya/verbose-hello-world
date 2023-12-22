package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
class JavaNetTest {

    @Test
    void __IPv4() {
        final var host = "127.0.0.1";
        final InetAddress addr;
        try {
            addr = InetAddress.getByName(host);
        } catch (final IOException ioe) {
            log.error("failed to get address by {}", host, ioe);
            return;
        }
        log.debug("addr: {} {}", addr, addr.getClass());
        final var port = 63004;
        // -----------------------------------------------------------------------------------------
        final var serverThread = new Thread(() -> {
            final var endpoint = new InetSocketAddress(addr, port);
            try {
                try (var server = new ServerSocket()) {
                    server.bind(endpoint);
                    log.debug("bound to {}", server.getLocalSocketAddress());
                    try (var client = server.accept()) {
                        log.debug("accepted from {}", client.getRemoteSocketAddress());
                    }
                }
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
        serverThread.start();
        // -----------------------------------------------------------------------------------------
        final var clientThread = new Thread(() -> {
            final var endpoint = new InetSocketAddress(addr, port);
            try (var client = new Socket()) {
                client.connect(endpoint);
                log.debug("connected to {}", client.getRemoteSocketAddress());
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
        clientThread.start();
    }

    @Test
    void __IPv6()
            throws IOException {
        final var host = "::1";
        final InetAddress addr;
        try {
            addr = InetAddress.getByName(host);
        } catch (final IOException ioe) {
            log.error("failed to get address by {}", host, ioe);
            return;
        }
        log.debug("addr: {} {}", addr, addr.getClass());
        final var port = 63006;
        // -----------------------------------------------------------------------------------------
        final var serverThread = new Thread(() -> {
            final var endpoint = new InetSocketAddress(addr, port);
            try {
                try (var server = new ServerSocket()) {
                    server.bind(endpoint);
                    log.debug("bound to {}", server.getLocalSocketAddress());
                    try (var client = server.accept()) {
                        log.debug("accepted from {}", client.getRemoteSocketAddress());
                    }
                }
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
        serverThread.start();
        // -----------------------------------------------------------------------------------------
        final var clientThread = new Thread(() -> {
            try {
                try (var client = new Socket()) {
                    client.connect(new InetSocketAddress(addr, port));
                    log.debug("connected to {}", client.getRemoteSocketAddress());
                }
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
        clientThread.start();
    }
}
