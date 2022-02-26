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
import java.net.InetSocketAddress;
import java.nio.file.Path;

import static com.github.jinahya.hello.HelloWorldClientTcp.clients;
import static com.github.jinahya.hello.IHelloWorldServerUtils.startReadingPortNumber;
import static java.net.InetAddress.getLoopbackAddress;
import static java.nio.file.Files.createTempDirectory;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class HelloWorldServerTcpTest {

    @Test
    void test(@TempDir final Path tempDir)
            throws IOException, InterruptedException {
        final var host = getLoopbackAddress();
        final var dir = createTempDirectory(tempDir, null);
        final var thread = startReadingPortNumber(dir, p -> {
            final var endpoint = new InetSocketAddress(host, p);
            clients(4, endpoint, s -> {
                log.debug("[C] received: {}", s);
                assertNotNull(s);
            });
        });
        try (var server = new HelloWorldServerTcp()) {
            final var endpoint = new InetSocketAddress(host, 0);
            try {
                server.open(endpoint, dir);
            } catch (final IOException ioe) {
                log.error("failed to open server", ioe);
                thread.interrupt();
                throw ioe;
            }
            thread.join();
        }
    }
}
