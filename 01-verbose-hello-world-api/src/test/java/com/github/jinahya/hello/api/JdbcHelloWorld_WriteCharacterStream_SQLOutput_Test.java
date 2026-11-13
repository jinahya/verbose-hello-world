package com.github.jinahya.hello.api;

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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.JdbcHelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link JdbcHelloWorld#writeCharacterStream(SQLOutput)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see SQLOutput#writeCharacterStream(Reader)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
 */
@DisplayName("JdbcHelloWorld.writeCharacterStream(SQLOutput)")
@Slf4j
class JdbcHelloWorld_WriteCharacterStream_SQLOutput_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#writeCharacterStream(SQLOutput)
     * writeCharacterStream(output)} method throws a {@link NullPointerException} when the
     * {@code output} argument is {@code null}.
     */
    @DisplayName("throws NPE / output is null")
    @Test
    void _ThrowNullPointerException_OutputIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final SQLOutput output = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.writeCharacterStream(output));
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#writeCharacterStream(SQLOutput)
     * writeCharacterStream(output)} method invokes {@link SQLOutput#writeCharacterStream(Reader)
     * output.writeCharacterStream(reader)} with a reader, which is still readable after the method
     * returns, that yields the {@code "hello, world"} characters, and returns the {@code output}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_index_sets_hello_world_bytes(service());
        final var output = mock(SQLOutput.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.writeCharacterStream(output);
        // ------------------------------------------------------------------------------------ then
        set_array12_index0_invoked_once(service());
        final var captor = ArgumentCaptor.forClass(Reader.class);
        verify(output, times(1)).writeCharacterStream(captor.capture());
        final var sink = new StringWriter();
        captor.getValue().transferTo(sink);
        assertEquals(hello_world_string(), sink.toString());
        assertSame(output, result);
    }
}
