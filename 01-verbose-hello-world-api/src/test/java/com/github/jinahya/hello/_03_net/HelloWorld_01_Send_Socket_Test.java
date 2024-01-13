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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A class for testing {@link HelloWorld#send(Socket) send(socket)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_01_Send_Socket_Test extends _HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#send(Socket) send(socket)} method throws a
     * {@link NullPointerException} when the {@code socket} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the socket argument is null"""
    )
    @Test
    void _ThrowNullPointerException_SocketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Socket socket = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(socket)
        );
    }

    @BeforeEach
    void beforeEach() throws IOException {
        writeStream_willReturnStream();
    }

    /**
     * Verifies that the {@link HelloWorld#send(Socket) send(socket)} method invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns the {@code socket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke write(socket.getOutputStream())")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> i.getArgument(0, OutputStream.class))
                .given(service)
                .write(ArgumentMatchers.any(OutputStream.class));
        final var socket = Mockito.mock(Socket.class);                 // <1>
        final var stream = Mockito.mock(OutputStream.class);           // <2>
        BDDMockito.given(socket.getOutputStream()).willReturn(stream); // <3>
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(socket);
        // ------------------------------------------------------------------------------------ then
        // TODO: verify, socket.getOutputStream() invoked, once
        // TODO: verify, service.write(stream) invoked, once
        Assertions.assertSame(socket, result);
    }
}
