package com.github.jinahya.hello.api._java_net_http;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
import org.mockito.*;

import java.net.*;
import java.net.http.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link HelloWorld#method(HttpRequest.Builder, String) method(builder, method)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("method(builder, method)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Method_HttpRequestBuilder_String_Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("should throw a <NullPointerException> when the <builder> argument is <null>")
    @Test
    void _ThrowNullPointerException_BuilderIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final HttpRequest.Builder builder = null;
        final var method = "";
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.method(builder, method));
    }

    @DisplayName("should throw a <NullPointerException> when the <method> argument is <null>")
    @Test
    void _ThrowNullPointerException_MethodIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var builder = mock(HttpRequest.Builder.class);
        final String method = null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.method(builder, method));
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("should invoke <builder.method(method, publisher)>, and return the <builder>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var builder = mock(HttpRequest.Builder.class);
        final var method = "WHATEVER";
        try (var mockStatic = mockStatic(HttpRequest.BodyPublishers.class,
                                         Mockito.CALLS_REAL_METHODS)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.method(builder, method);
            // -------------------------------------------------------------------------------- then
            final var array = set_array12_invoked_once(service);
            mockStatic.verify(
                    () -> HttpRequest.BodyPublishers.ofByteArray(array),
                    times(1)
            );
            assertSame(builder, result);
        }
    }

    @DisplayName("should send <hello-world-bytes> through a real <HttpClient> request")
    @畵蛇添足
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var builder = i.getArgument(0, HttpRequest.Builder.class);
            final var method = i.getArgument(1, String.class);
            builder.method(
                    method,
                    HttpRequest.BodyPublishers.ofByteArray(
                            "hello, world".getBytes(StandardCharsets.US_ASCII)
                    )
            );
            return builder;
        }).when(service).method(Mockito.<HttpRequest.Builder>any(), Mockito.anyString());
        HelloWorld__TestUtils.executeWithHttpEchoStarted(p -> () -> {
            final HttpClient.Version version;
            {
                final var versions = HttpClient.Version.values();
                version = versions[ThreadLocalRandom.current().nextInt(versions.length)];
            }
            try (var client = HttpClient.newBuilder().version(version).build()) {
                final var builder = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + p));
                // ---------------------------------------------------------------------------- when
                service.method(builder, "POST");
                builder.header("Content-Type", "text/plain");
                final var request = builder.build();
                final var response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofByteArray()
                );
                System.out.printf(
                        "%s %d%n",
                        response.version() == HttpClient.Version.HTTP_1_1 ? "HTTP/1.1" : "HTTP/2",
                        response.statusCode()
                );
                response.headers().map().forEach((k, v) -> {
                    v.forEach(e -> {
                        System.out.printf("%s: %s%n", k, e);
                    });
                });
                System.out.printf("%n%s%n", new String(response.body(), StandardCharsets.US_ASCII));
            }
        });
    }
}
