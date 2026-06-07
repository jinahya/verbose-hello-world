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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.net.http.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#sendBinary(WebSocket, boolean) send(socket, last)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("sendBinary(socket, last)")
@Slf4j
class AsynchronousHelloWorld_SendBinary_WebSocket_Test
        extends AsynchronousHelloWorld__Test<HelloWorld, ExecutorHelloWorld<HelloWorld>> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    AsynchronousHelloWorld_SendBinary_WebSocket_Test() {
        super(HelloWorld.class, s -> new ExecutorHelloWorld<>(s, Runnable::run));
    }

//    // ---------------------------------------------------------------------------------------------
//    @DisplayName("""
//            should throw a <NullPointerException>
//            when the <socket> argument is <null>""")
//    @Test
//    void _ThrowNullPointerException_SocketIsNull() {
//        // ----------------------------------------------------------------------------------- given
//        final var asynchronousService = asynchronousService();
//        final WebSocket socket = null;
//        final var last = ThreadLocalRandom.current().nextBoolean();
//        // ------------------------------------------------------------------------------- when/then
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> asynchronousService.sendBinary(socket, last)
//        );
//    }
//
//    // ---------------------------------------------------------------------------------------------
//    @DisplayName("should invoke socket.sendBinary(buffer, last) and return the result")
//    @Test
//    void __() {
//        // what we're testing here?
//        // - we prepare a socket
//        // - we invoke the send(socket, last) method with the socket
//        // - we get the result future which should be not null
//        // ----------------------------------------------------------------------------------- given
//        final var asynchronousService = asynchronousService();
//        final var socket = Mockito.mock(WebSocket.class);
//        final var future = new CompletableFuture<WebSocket>();
//        Mockito.when(socket.sendBinary(Mockito.any(ByteBuffer.class), Mockito.anyBoolean()))
//                .thenReturn(future);
//        final var last = ThreadLocalRandom.current().nextBoolean();
//        // ------------------------------------------------------------------------------------ when
//        final var result = asynchronousService.sendBinary(socket, last);
//        // ------------------------------------------------------------------------------------ then
//        Mockito.verify(socket, Mockito.times(1)).sendBinary(
//                Mockito.argThat(b -> b.remaining() == HelloWorld.BYTES),
//                Mockito.eq(last)
//        );
//        Assertions.assertNotNull(result);
//    }
//
//    @Test
//    void _添足_畵蛇()
//            throws Exception {
//        final var asynchronousService = asynchronousService();
//        final var port = 8887;
//
//        // 1. Minimal Server that only logs
//        final var server = new WebSocketServer(new InetSocketAddress(port)) { // @formatter:off
//            @Override public void onOpen(final org.java_websocket.WebSocket conn,
//                               final ClientHandshake handshake) {
//            }
//            @Override public void onClose(final org.java_websocket.WebSocket conn, final int code,
//                                          final String reason, final boolean remote) { }
//            @Override public void onError(final org.java_websocket.WebSocket conn,
//                                          final Exception ex) { }
//            @Override public void onStart() { }
//            @Override public void onMessage(final org.java_websocket.WebSocket conn,
//                                            final String message) { }
//            @Override public void onMessage(final org.java_websocket.WebSocket conn,
//                                            final ByteBuffer message) {
//                super.onMessage(conn, message); // empty
//                log.debug("message: {}", StandardCharsets.US_ASCII.decode(message));
//            } // @formatter:on
//        };
//        server.start();
//        try {
//            final var client = HttpClient.newHttpClient()
//                    .newWebSocketBuilder()
//                    .buildAsync(URI.create("ws://localhost:" + port), new WebSocket.Listener() {
//                    })
//                    .join();
//            final var future = asynchronousService.sendBinary(client, true);
//            future.join();
//            client.sendClose(WebSocket.NORMAL_CLOSURE, "ok").join();
//        } finally {
//            server.stop();
//        }
//    }
//
//    @Test
//    void __vertx()
//            throws Exception {
//        final var asynchronousService = asynchronousService();
//        final var vertx = Vertx.vertx();
//        final var received = new CompletableFuture<Void>();
//        final var port = 8888;
//        final var server = vertx.createHttpServer()
//                .webSocketHandler(sws -> {
//                    sws.handler(b -> {
//                        log.debug("received: {}", b);
//                        received.complete(null);
//                    });
//                })
//                .listen(port)
//                .toCompletionStage()
//                .toCompletableFuture()
//                .get();
//        final var client = HttpClient.newHttpClient()
//                .newWebSocketBuilder()
//                .buildAsync(URI.create("ws://localhost:" + port), new WebSocket.Listener() {
//                })
//                .join();
//        asynchronousService.sendBinary(client, true);
//        received.get(5, TimeUnit.SECONDS);
//        server.close().toCompletionStage().toCompletableFuture().get();
//        vertx.close().toCompletionStage().toCompletableFuture().get();
//    }
//
//    // TODO: add SpringBoot WebSocket
//
//    // TODO: add Tyrus
}
