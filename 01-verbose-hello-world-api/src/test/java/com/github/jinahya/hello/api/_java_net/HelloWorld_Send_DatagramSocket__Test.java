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

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("send(socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_DatagramSocket__Test extends HelloWorld__Test {

    @BeforeEach
    void __stubService() throws IOException {
        doAnswer(i -> {
            final var socket = i.getArgument(0, DatagramSocket.class);
            socket.send(new DatagramPacket(hello_world_byte_array(), HelloWorld.BYTES));
            return socket;
        }).when(service()).send(argThat(DatagramSocket::isConnected));
    }

    @DisplayName("should send <hello-world-bytes> through a real connected <DatagramSocket>")
    @Test
    void __() throws IOException {
        try (var server = new DatagramSocket(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0))) {
            Thread.ofPlatform().start(() -> {
                try {
                    final DatagramPacket packet = new DatagramPacket(
                            new byte[HelloWorld.BYTES], // <buf>
                            HelloWorld.BYTES            // <length>
                    );
                    server.receive(packet);
                    final var decoded = new String(
                            packet.getData(),         // <bytes>
                            packet.getOffset(),       // <offset>
                            packet.getLength(),       // <length>
                            StandardCharsets.US_ASCII // <charset>
                    );
                    log.debug("'{}' received from {}", decoded, packet.getSocketAddress());
                } catch (final IOException ioe) {
                    throw new UncheckedIOException(ioe);
                }
            });
            try (var client = new DatagramSocket()) {
                client.connect(server.getLocalSocketAddress());
                service().send(client);
            }
        }
    }
}
