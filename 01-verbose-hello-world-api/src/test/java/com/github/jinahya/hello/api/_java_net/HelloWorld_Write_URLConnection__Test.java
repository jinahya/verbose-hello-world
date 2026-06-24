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
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#write(URLConnection) write(connection)} method with real
 * implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("HelloWorld.write(URLConnection)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_URLConnection__Test extends HelloWorld__Test {

    /**
     * Stubs {@code service.write(connection)} to write the {@code hello-world-bytes} to the
     * connection's output stream before each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __stubService() throws IOException {
        doAnswer(i -> {
            final var connection = i.getArgument(0, URLConnection.class);
            connection.getOutputStream().write(hello_world_byte_array());
            return connection;
        }).when(service()).write(ArgumentMatchers.<URLConnection>notNull());
    }

    /**
     * Verifies that the method sends {@code hello-world-bytes} through a real
     * {@link HttpURLConnection}.
     */
    @DisplayName("happy path")
    @Test
    void __() {
        executeWithHttpEchoStarted(p -> () -> {
            final var uri = URI.create("http://localhost:" + p);
            final var connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Content-Length", Integer.toString(HelloWorld.BYTES));
            connection.setRequestProperty("Accept", "text/plain");
            try {
                connection.connect();
                service().write(connection);
                connection.getOutputStream().flush();
                final var status = connection.getResponseCode();
                assertEquals(200, status);
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
