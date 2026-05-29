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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_Socket__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorld__TestUtils.send_socket_sends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class SocketTest {

        @Test
        void ___() throws IOException {
            try (var server = new ServerSocket()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                Thread.ofPlatform().daemon().start(() -> {
                    try (var client = server.accept()) {
                        final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                        final var string = new String(bytes, StandardCharsets.US_ASCII);
                        log.debug("'{}' received from {}", string, client.getRemoteSocketAddress());
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = new Socket()) {
                    client.connect(server.getLocalSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                }
            }
        }
    }
}
