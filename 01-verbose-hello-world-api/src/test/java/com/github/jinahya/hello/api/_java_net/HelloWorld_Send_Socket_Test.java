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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#send(Socket) send(socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_Socket_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#send(Socket) send(socket)} method throws a
     * {@link NullPointerException} when the {@code socket} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <socket> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = (Socket) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(socket)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#send(Socket)} method invokes
     * {@link HelloWorld#write(OutputStream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns the {@code socket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write(socket.outputStream)>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.any(OutputStream.class));
        final var socket = Mockito.mock(Socket.class);                 // <1>
        final var stream = Mockito.mock(OutputStream.class);           // <2>
        Mockito.when(socket.getOutputStream()).thenReturn(stream);     // <3>
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket);
        // ------------------------------------------------------------------------------------ then
//        Mockito.verify(service, Mockito.times(1)).write(stream);
        Assertions.assertSame(socket, result);
    }

    @Test
    void _添足_畵蛇() throws IOException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var socket = i.getArgument(0, Socket.class);
            socket.getOutputStream().write("hello, world".getBytes(StandardCharsets.US_ASCII));
            socket.getOutputStream().flush();
            return socket;
        }).when(service).send(ArgumentMatchers.<Socket>notNull());
        // ----------------------------------------------------------------------------- when / then
        try (var server = new ServerSocket()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
            final var thread = Thread.ofPlatform().daemon().start(() -> {
                try (var client = server.accept()) {
                    final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                    log.debug("read: {}", new String(bytes, StandardCharsets.US_ASCII));
                } catch (final IOException ioe) {
                    throw new RuntimeException("failed to accept/read", ioe);
                }
            });
            try (var client = new Socket()) {
                client.connect(server.getLocalSocketAddress());
                service.send(client);
                client.getOutputStream().flush();
            }
            thread.join();
        }
    }
}
