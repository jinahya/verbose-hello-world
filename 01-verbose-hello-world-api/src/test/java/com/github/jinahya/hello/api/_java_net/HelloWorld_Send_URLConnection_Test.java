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
import com.github.jinahya.hello.api.畵蛇添足;
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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#send(URLConnection) send(connection)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(URLConnection)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_URLConnection_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#send(URLConnection) send(connection)} method throws a
     * {@link NullPointerException} when the {@code connection} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <connection> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ConnectionIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final URLConnection connection = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(connection)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#send(URLConnection) send(connection)} method invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} with
     * {@link URLConnection#getOutputStream() connection.outputStream}, and returns the
     * {@code connection}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write(connection.outputStream)> and return <connection>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.any(OutputStream.class));
        final var connection = Mockito.mock(URLConnection.class);        // <1>
        final var stream = Mockito.mock(OutputStream.class);             // <2>
        Mockito.when(connection.getOutputStream()).thenReturn(stream);   // <3>
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(connection);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write(stream);         // <4>
        Assertions.assertSame(connection, result);                       // <5>
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write("hello, world".getBytes(StandardCharsets.US_ASCII));
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        // ----------------------------------------------------------------------------- when / then
        try (var server = new ServerSocket()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
            final var thread = Thread.ofPlatform().daemon().start(() -> {
                try (var client = server.accept()) {
                    final var bytes = client.getInputStream().readAllBytes();
                    log.debug("read: ({})\n{}", bytes.length,
                              new String(bytes, StandardCharsets.ISO_8859_1));
                } catch (final IOException ioe) {
                    throw new RuntimeException("failed to accept/read", ioe);
                }
            });
            final var uri = URI.create("http://localhost:" + server.getLocalPort());
            final var connection = uri.toURL().openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(false);
            try {
                connection.connect();
                service.send(connection);
                connection.getOutputStream().flush();
            } finally {
                ((HttpURLConnection) connection).disconnect();
            }
            thread.join();
        }
    }
}
