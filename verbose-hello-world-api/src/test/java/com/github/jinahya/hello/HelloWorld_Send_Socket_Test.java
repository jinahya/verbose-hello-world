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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for testing {@link HelloWorld#send(Socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Send_Socket_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#send(Socket)} method throws a {@link NullPointerException} when the {@code socket}
     * argument is {@code null}.
     */
    @DisplayName("send(socket) throws NullPointerException when socket is null")
    @Test
    void sendSocket_NullPointerException_SocketIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.send(null));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method invokes the {@link HelloWorld#write(OutputStream)} method
     * with what specified socket's {@link Socket#getOutputStream()} method returns.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) invokes write(socket.outputStream)")
    @Test
    void sendSocket_InvokeWriteStreamWithSocketOutputStream_() throws IOException {
        final Socket socket = spy(new Socket());              // <1>
        final OutputStream stream = mock(OutputStream.class); // <2>
        doReturn(stream).when(socket).getOutputStream();      // <3>
    }
}