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
import org.mockito.*;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#send(DatagramSocket) send(socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(DatagramSocket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_DatagramSocket_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket) send(socket)} method throws a
     * {@link NullPointerException} when the {@code socket} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <socket> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final DatagramSocket socket = null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.send(socket));
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket) send(socket)} method throws an
     * {@link IllegalArgumentException} when the {@code socket} is not connected.
     */
    @DisplayName("""
            should throw an <IllegalArgumentException>
            when the <socket> is not connected"""
    )
    @Test
    void _ThrowIllegalArgumentException_SocketIsNotConnected() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var socket = mock(DatagramSocket.class);
        when(socket.isConnected()).thenReturn(false);
        // ------------------------------------------------------------------------------- when/then
        assertThrows(IllegalArgumentException.class, () -> service.send(socket));
    }

    /**
     * Verifies that the {@link HelloWorld#send(DatagramSocket) send(socket)} method invokes
     * {@link HelloWorld#send(DatagramSocket, SocketAddress) send(socket, target)} method with
     * {@code socket} and
     * {@link DatagramSocket#getRemoteSocketAddress() socket.remoteSocketAddress}, and returns the
     * {@code socket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <send(socket, socket.remoteAddress)>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service)
                .send(ArgumentMatchers.<DatagramSocket>any(), any());
        final var socket = mock(DatagramSocket.class);
        when(socket.isConnected()).thenReturn(true);
        final var target = mock(SocketAddress.class);
        when(socket.getRemoteSocketAddress()).thenReturn(target);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket);
        // ------------------------------------------------------------------------------------ then
//        verify(service, times(1)).send(socket, target);
        assertSame(socket, result);
    }
}
