package com.github.jinahya.hello.api._java_net;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_Socket_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.send_socket_sends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class SocketTest {

        @Test
        void ___() throws IOException, InterruptedException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            try (var server = new ServerSocket()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                final var thread = Thread.ofPlatform().daemon().start(() -> {
                    try (var client = server.accept()) {
                        final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                        log.debug("received: {}", new String(bytes, StandardCharsets.US_ASCII));
                    } catch (final IOException ioe) {
                        throw new RuntimeException("failed to accept/read", ioe);
                    }
                });
                try (var client = new Socket()) {
                    client.connect(server.getLocalSocketAddress());
                    // ------------------------------------------------------------------------ when
                    service.send(client);
                    client.getOutputStream().flush();
                }
                thread.join();
            }
        }
    }
}
