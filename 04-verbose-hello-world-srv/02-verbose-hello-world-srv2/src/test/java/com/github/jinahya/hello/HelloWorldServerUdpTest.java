package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv2
 * %%
 * Copyright (C) 2018 - 2021 Jinahya, Inc.
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
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test(@TempDir final Path tempDir)
            throws IOException, InterruptedException {
        final var host = InetAddress.getLoopbackAddress();
        final Path dir = Files.createTempDirectory(tempDir, null);
        final Thread thread = new Thread(() -> {
            final int port;
            try {
                port = IHelloWorldServerUtils.readPortNumber(dir);
            } catch (final IOException ioe) {
                log.error("failed to read port number", ioe);
                return;
            } catch (final InterruptedException ie) {
                log.debug("interrupted while reading port number", ie);
                Thread.currentThread().interrupt();
                return;
            }
            HelloWorldClientUdp.clients(
                    4,
                    new InetSocketAddress(host, port),
                    s -> {
                        log.debug("[C] received: {}", s);
                        assertNotNull(s);
                    }
            );
        });
        thread.start();
        try (var server = new HelloWorldServerUdp()) {
            try {
                server.open(new InetSocketAddress(host, 0), dir);
            } catch (final IOException ioe) {
                log.error("failed to open the server", ioe);
                thread.interrupt();
                throw ioe;
            }
            thread.join();
        }
    }
}
