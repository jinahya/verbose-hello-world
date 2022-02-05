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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A class for testing {@link HelloWorld#send(Socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_04_Send_Socket_Arguments_Test
 */
@Slf4j
class HelloWorld_04_Send_Socket_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method invokes the
     * {@link HelloWorld#write(OutputStream) write(stream)} method with {@link
     * Socket#getOutputStream() socket.outputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) invokes write(socket.outputStream)")
    @Test
    void send_InvokeWriteStreamWithSocketOutputStream_() throws IOException {
        final Socket socket = Mockito.spy(new Socket());              // <1>
        final OutputStream stream = Mockito.mock(OutputStream.class); // <2>
        Mockito.lenient().doReturn(stream)                            // <3>
                .when(socket)
                .getOutputStream();
        helloWorld().send(socket);                                    // <4>
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method returns the
     * {@code socket} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) returns socket")
    @Test
    void send_ReturnSocket_() throws IOException {
        final Socket expected = Mockito.spy(new Socket());
        Mockito.lenient().doReturn(Mockito.mock(OutputStream.class))
                .when(expected)
                .getOutputStream();
        final Socket actual = helloWorld().send(expected);
        Assertions.assertSame(expected, actual);
    }
}