package com.github.jinahya.hello.api._java_net_http;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.DefaultAsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link AsynchronousHelloWorld#send(WebSocket, boolean) send(socket, last)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(WebSocket, boolean)")
@Slf4j
class AsynchornousHelloWorld_Send_WebSocket_Test extends DefaultAsynchronousHelloWorldTest {

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    AsynchornousHelloWorld_Send_WebSocket_Test() {
        super();
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("""
            should throw a <NullPointerException>
            when the <socket> argument is <null>""")
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final WebSocket socket = null;
        final var last = ThreadLocalRandom.current().nextBoolean();
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(socket, last)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("should invoke socket.sendBinary(buffer, last) and return the result")
    @Test
    void __() {
        // what we're testing here?
        // - we prepare a socket
        // - we invoke the send(socket, last) method with the socket
        // - we get the result future which should be not null
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = Mockito.mock(WebSocket.class);
        final var future = new CompletableFuture<WebSocket>();
        Mockito.when(socket.sendBinary(Mockito.any(ByteBuffer.class), Mockito.anyBoolean()))
                .thenReturn(future);
        final var last = ThreadLocalRandom.current().nextBoolean();
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket, last);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(socket, Mockito.times(1)).sendBinary(
                Mockito.argThat(b -> b.remaining() == HelloWorld.BYTES),
                Mockito.eq(last)
        );
        Assertions.assertNotNull(result);
    }

    @Test
    void _添足_畵蛇() throws Exception {
        final var service = service();
        final var port = 8887;

        // 1. Minimal Server that only logs
        final var server = new WebSocketServer(new InetSocketAddress(port)) { // @formatter:off
            @Override public void onOpen(final org.java_websocket.WebSocket conn,
                               final ClientHandshake handshake) {
            }
            @Override public void onClose(final org.java_websocket.WebSocket conn, final int code,
                                          final String reason, final boolean remote) { }
            @Override public void onError(final org.java_websocket.WebSocket conn,
                                          final Exception ex) { }
            @Override public void onStart() { }
            @Override public void onMessage(final org.java_websocket.WebSocket conn,
                                            final String message) { }
            @Override public void onMessage(final org.java_websocket.WebSocket conn,
                                            final ByteBuffer message) {
                super.onMessage(conn, message); // empty
                log.debug("message: {}", StandardCharsets.US_ASCII.decode(message));
            } // @formatter:on
        };
        server.start();
        try {
            final var client = HttpClient.newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(URI.create("ws://localhost:" + port), new WebSocket.Listener() {
                    })
                    .join();
            final var future = service.send(client, true);
            future.join();
            client.sendClose(WebSocket.NORMAL_CLOSURE, "ok").join();
        } finally {
            server.stop();
        }
    }
}
