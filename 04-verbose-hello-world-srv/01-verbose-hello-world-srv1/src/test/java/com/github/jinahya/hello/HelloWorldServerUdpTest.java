package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv1
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test(@TempDir Path tempDir) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var dir = Files.createTempDirectory(tempDir, null);
        var thread = new Thread(() -> {
            int port;
            try {
                port = HelloWorldServerUtils.readPortNumber(dir);
            } catch (IOException ioe) {
                log.error("failed to read port number from " + dir, ioe);
                return;
            } catch (InterruptedException ie) {
                log.error("interrupted while reading port number from " + dir, ie);
                Thread.currentThread().interrupt();
                return;
            }
            var endpoint = new InetSocketAddress(host, port);
            try {
                HelloWorldClientUdp.connect(4, endpoint, s -> {
                    log.debug("[C] received: {}", s);
                    Assertions.assertNotNull(s);
                });
            } catch (IOException ioe) {
                log.error("failed to connect", ioe);
            }
        });
        thread.start();
        try (var server = new HelloWorldServerUdp()) {
            var endpoint = new InetSocketAddress(host, 0);
            try {
                server.open(endpoint, dir);
            } catch (IOException ioe) {
                log.error("failed to open the server", ioe);
                thread.interrupt();
                throw ioe;
            }
            thread.join();
        }
    }
}
