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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#write(URLConnection) write(connection)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(URLConnection)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_URLConnection_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(URLConnection) write(connection)} method throws a
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
                () -> service.write(connection)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(URLConnection) write(connection)} method invokes
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
        final var connection = Mockito.mock(URLConnection.class);
        final var stream = Mockito.mock(OutputStream.class);
        Mockito.when(connection.getOutputStream()).thenReturn(stream);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(connection);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write(stream);
        Assertions.assertSame(connection, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var connection = i.getArgument(0, URLConnection.class);
            connection.getOutputStream().write("hello, world".getBytes(StandardCharsets.US_ASCII));
            return connection;
        }).when(service).write(ArgumentMatchers.<URLConnection>notNull());
        HelloWorldTestUtils.executeWithHttpServerStarted(p -> () -> {
            final var uri = URI.create("http://localhost:" + p);
            final var connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "text/plain");
            try {
                connection.connect();
                // ---------------------------------------------------------------------------- when
                service.write(connection);
                connection.getOutputStream().flush();
                connection.getOutputStream().close();
                // ---------------------------------------------------------------------------- then
                final var status = connection.getResponseCode();
                Assertions.assertEquals(200, status);
                System.out.printf("%s%n", connection.getHeaderField(0));
                connection.getHeaderFields().entrySet().stream().skip(1L).forEach(
                        e -> e.getValue().forEach(
                                v -> System.out.printf("%s: %s%n", e.getKey(), v)
                        )
                );
                try (final var is = connection.getInputStream()) {
                    final var bytes = is.readAllBytes();
                    System.out.printf("%n%s%n", new String(bytes, StandardCharsets.US_ASCII));
                }
            } finally {
                connection.disconnect();
            }
        });
    }
}
