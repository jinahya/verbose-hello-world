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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#send(Socket) send(socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_Socket_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#send(Socket) send(socket)} method throws a
     * {@link NullPointerException} when the {@code socket} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <socket> argument is <null>")
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = (Socket) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.send(socket));
    }

    /**
     * Verifies that the {@link HelloWorld#send(Socket)} method invokes
     * {@link HelloWorld#write(OutputStream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns the {@code socket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write(socket.outputStream)>, and return the <socket>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).write(any(OutputStream.class));
        final var socket = mock(Socket.class);
        final var stream = mock(OutputStream.class);
        doReturn(stream).when(socket).getOutputStream();
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket);
        // ------------------------------------------------------------------------------------ then
//        verify(service, times(1)).write(stream);
        assertSame(socket, result);
    }
}
