package com.github.jinahya.hello.api._java_sql;

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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.JdbcHelloWorld#setNClob(CallableStatement, String)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see CallableStatement#setNClob(String, Reader)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
 */
@DisplayName("JdbcHelloWorld.setNClob(CallableStatement, String)")
@Slf4j
class JdbcHelloWorld_SetNClob_CallableStatement_String_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.JdbcHelloWorld#setNClob(CallableStatement, String)
     * setNClob(callableStatement, parameterName)} method throws a {@link NullPointerException} when
     * the {@code callableStatement} argument is {@code null}.
     */
    @DisplayName("throws NPE / callableStatement is null")
    @Test
    void _ThrowNullPointerException_CallableStatementIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final CallableStatement callableStatement = null;
        final var parameterName = "name";
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.setNClob(callableStatement, parameterName)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.JdbcHelloWorld#setNClob(CallableStatement, String)
     * setNClob(callableStatement, parameterName)} method throws a {@link NullPointerException} when
     * the {@code parameterName} argument is {@code null}.
     */
    @DisplayName("throws NPE / parameterName is null")
    @Test
    void _ThrowNullPointerException_ParameterNameIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var callableStatement = mock(CallableStatement.class);
        final String parameterName = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.setNClob(callableStatement, parameterName)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.JdbcHelloWorld#setNClob(CallableStatement, String)
     * setNClob(callableStatement, parameterName)} method invokes
     * {@link CallableStatement#setNClob(String, Reader) callableStatement.setNClob(parameterName,
     * reader)} with a reader, which is still readable after the method returns, that yields the
     * {@code "hello, world"} characters, and returns the {@code callableStatement}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_hello_world_bytes(service());
        final var callableStatement = mock(CallableStatement.class);
        final var parameterName = "name";
        // ------------------------------------------------------------------------------------ when
        final var result = service.setNClob(callableStatement, parameterName);
        // ------------------------------------------------------------------------------------ then
        set_array12_invoked_once(service());
        final var captor = ArgumentCaptor.forClass(Reader.class);
        verify(callableStatement, times(1)).setNClob(eq(parameterName), captor.capture());
        final var sink = new StringWriter();
        captor.getValue().transferTo(sink);
        assertEquals(hello_world_string(), sink.toString());
        assertSame(callableStatement, result);
    }
}
