package com.github.jinahya.hello.api._java_net_http;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.畵蛇添足;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#method(HttpRequest.Builder, String)
 * method(builder, method)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("method(HttpRequest.Builder, String)")
@Slf4j
class HelloWorld_Method_HttpRequest_Builder_Test extends HelloWorldTest {

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    HelloWorld_Method_HttpRequest_Builder_Test() {
        super();
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("""
            should throw a <NullPointerException>
            when the <builder> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_BuilderIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final HttpRequest.Builder builder = null;
        final var method = "POST";
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.method(builder, method)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <method> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_MethodIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var builder = HttpRequest.newBuilder();
        final String method = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.method(builder, method)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("should invoke method(method, publisher)")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        stub_set_array_will_return_the_array();
        final var builder = HttpRequest.newBuilder();
        final var method = "WHATEVER";
        try (var mockStatic = Mockito.mockStatic(HttpRequest.BodyPublishers.class,
                                                 Mockito.CALLS_REAL_METHODS)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.method(builder, method);
            // -------------------------------------------------------------------------------- then
            final var array = verify_set_array12_invoked_once();
            mockStatic.verify(
                    () -> HttpRequest.BodyPublishers.ofByteArray(array),
                    Mockito.times(1)
            );
            Assertions.assertSame(builder, result);
        }
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        stub_set_array_will_set_actual_hello_world_bytes();
        final var server = HttpServer.create(new InetSocketAddress(0), 0);
        final var port = server.getAddress().getPort();
        final var path = "/post";
        server.createContext(path, h -> {
            try (h) {
                final var input = h.getRequestBody().readAllBytes();
                log.debug("request: {}", new String(input, StandardCharsets.US_ASCII));
                h.sendResponseHeaders(204, -1); // -1 means no body
            }
        });
        try {
            server.start();
            try (var client = HttpClient.newHttpClient()) {
                final var builder = HttpRequest.newBuilder();
                service.method(builder, "POST");
                final var request = builder
                        .uri(URI.create("http://localhost:" + port + path))
                        .build();
                // ---------------------------------------------------------------------------- when
                client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            }
        } finally {
            server.stop(0);
        }
    }
}
