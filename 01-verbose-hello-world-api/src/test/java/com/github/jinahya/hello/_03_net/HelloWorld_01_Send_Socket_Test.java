package com.github.jinahya.hello._03_net;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#send(Socket) send(socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Send_Socket_Arguments_Test
 */
@DisplayName("send(socket)")
@Slf4j
class HelloWorld_01_Send_Socket_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach()
            throws IOException {
        _stub_WriteStream_ToWrite12BytesAndReturnTheStream();
    }

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method invokes the
     * {@link HelloWorld#write(OutputStream) write(stream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns the {@code socket}.
     *
     * @throws IOException if an I/O error occurs.
     * @see org.mockito.Mockito#verifyNoMoreInteractions(Object...)
     */
    @DisplayName("(socket) -> write(socket.outputStream)")
    @Test
    void _InvokeWriteStreamWithSocketOutputStream_()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var socket = mock(Socket.class);                                     // <1>
        when(socket.getOutputStream()).thenReturn(mock(OutputStream.class)); // <3>
        // ------------------------------------------------------------------------------------ when
        var result = service.send(socket);                                   // <4>
        // ------------------------------------------------------------------------------------ then
        // TODO: Verify, once, service.write(socket.getOutputStream) invoked.
        assertSame(socket, result);
    }
}
