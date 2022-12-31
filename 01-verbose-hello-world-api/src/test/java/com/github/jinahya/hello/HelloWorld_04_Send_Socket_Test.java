package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#send(Socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_04_Send_Socket_Arguments_Test
 */
@DisplayName("send(socket)")
@Slf4j
class HelloWorld_04_Send_Socket_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#write(OutputStream) write(stream)} method to return the
     * {@code stream} argument.
     */
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnStream_WriteStream() throws IOException {
        var service = service();
        doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(any(OutputStream.class));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method invokes the
     * {@link HelloWorld#write(OutputStream) write(stream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("invokes write(socket.outputStream)")
    @Test
    void _InvokeWriteStreamWithSocketOutputStream_() throws IOException {
        // GIVEN
        var service = service();
        var socket = mock(Socket.class);                   // <1>
        var stream = mock(OutputStream.class);             // <2>
        when(socket.getOutputStream()).thenReturn(stream); // <3>
        // WHEN
        service.send(socket);                              // <4>
        // THEN: only, socket.getOutputStream() invoked    // <5>
        // TODO: Verify socket.getOutputStream invoked
        // THEN: once, write(stream) invoked               // <6>
        // TODO: Verify write(stream) invoked
    }

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method returns the {@code socket}
     * argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns socket")
    @Test
    void _ReturnSocket_() throws IOException {
        // GIVEN:
        var service = service();
        var socket = mock(Socket.class);
        var stream = mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(stream);
        // WHEN
        var actual = service.send(socket);
        // THEN
        assertSame(socket, actual);
    }

    @org.junit.jupiter.api.Disabled("enable when implemented")
    @DisplayName("12 bytes are written")
    @Test
    void _12BytesWritten_() throws IOException, InterruptedException {
        var service = service();
        doAnswer(i -> {
            OutputStream stream = i.getArgument(0);
            stream.write(new byte[BYTES]);
            return stream;
        }).when(service).write(any(OutputStream.class));
        try (var server = new ServerSocket()) {
            var addr = InetAddress.getLoopbackAddress();
            var port = 0;
            var endpoint = new InetSocketAddress(addr, port);
            server.bind(endpoint);
            log.debug("server bound to {}", server.getLocalSocketAddress());
            var thread = new Thread(() -> {
                try (var client = server.accept()) {
                    log.debug("accepted from {}", client.getRemoteSocketAddress());
                    service.send(client);
                } catch (IOException ioe) {
                    log.error("failed to accept/work", ioe);
                }
            });
            thread.start();
            try (var client = new Socket()) {
                client.connect(server.getLocalSocketAddress());
                log.debug("connected to {}", client.getRemoteSocketAddress());
                byte[] b = new byte[BYTES];
                int off;
                for (off = 0; !currentThread().isInterrupted() && off < b.length; ) {
                    int r = client.getInputStream().read(b, off, b.length - off);
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    off += r;
                }
                assertEquals(BYTES, off);
            } catch (IOException ioe) {
                log.error("failed to work with the server", ioe);
            }
            thread.join(SECONDS.toMillis(1L));
        }
    }
}
