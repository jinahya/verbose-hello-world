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
 * A class for testing {@link HelloWorld#write(URLConnection) write(connection)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(connection)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_URLConnection_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(URLConnection) write(connection)} method throws a
     * {@link NullPointerException} when the {@code connection} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <connection> argument is <null>")
    @Test
    void _ThrowNullPointerException_ConnectionIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final URLConnection connection = null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(connection));
    }

    /**
     * Verifies that the {@link HelloWorld#write(URLConnection) write(connection)} method invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} with
     * {@link URLConnection#getOutputStream() connection.outputStream}, and returns the
     * {@code connection}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write(connection.outputStream)>, and return the <connection>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).write(any(OutputStream.class));
        final var connection = mock(URLConnection.class);
        final var stream = mock(OutputStream.class);
        when(connection.getOutputStream()).thenReturn(stream);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(connection);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write(stream);
        assertSame(connection, result);
    }
}
