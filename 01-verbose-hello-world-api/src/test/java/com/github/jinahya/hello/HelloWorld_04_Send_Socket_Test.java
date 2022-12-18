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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * A class for testing {@link HelloWorld#send(Socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_04_Send_Socket_Arguments_Test
 */
@Slf4j
class HelloWorld_04_Send_Socket_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#write(OutputStream) write(stream)} method returns the {@code stream}
     * argument.
     */
    @DisplayName("write(stream) returns stream")
    @BeforeEach
    void stub_ReturnArray_SetArray() throws IOException {
        doAnswer(i -> i.getArgument(0)).when(service()).write(any(OutputStream.class));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method invokes the
     * {@link HelloWorld#write(OutputStream) write(stream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) invokes write(socket.outputStream)")
    @Test
    void _InvokeWriteStreamWithSocketOutputStream_() throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: Socket
        var socket = spy(new Socket());                            // <1>
        // GIVEN: OutputStream
        var stream = mock(OutputStream.class);                     // <2>
        lenient().doReturn(stream).when(socket).getOutputStream(); // <3>
        // WHEN
        service.send(socket);                                      // <4>
        // THEN: once, write(stream) invoked
    }

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method returns the {@code socket}
     * argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) returns socket")
    @Test
    void _ReturnSocket_() throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: Socket
        var socket = spy(new Socket());
        // GIVEN: OutputStream
        var stream = mock(OutputStream.class);
        lenient().doReturn(stream).when(socket).getOutputStream();
        // WHEN
        var actual = service.send(socket);
        // THEN
        assertSame(socket, actual);
    }
}
