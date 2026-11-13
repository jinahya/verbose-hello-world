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

import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateString(ResultSet, String)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ResultSet#updateString(String, String)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
 */
@DisplayName("JdbcHelloWorld.updateString(ResultSet, String)")
@Slf4j
class JdbcHelloWorld_UpdateString_ResultSet_String_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateString(ResultSet, String)
     * updateString(resultSet, columnLabel)} method throws a {@link NullPointerException} when the
     * {@code resultSet} argument is {@code null}.
     */
    @DisplayName("throws NPE / resultSet is null")
    @Test
    void _ThrowNullPointerException_ResultSetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final ResultSet resultSet = null;
        final var columnLabel = "label";
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.updateString(resultSet, columnLabel)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateString(ResultSet, String)
     * updateString(resultSet, columnLabel)} method throws a {@link NullPointerException} when the
     * {@code columnLabel} argument is {@code null}.
     */
    @DisplayName("throws NPE / columnLabel is null")
    @Test
    void _ThrowNullPointerException_ColumnLabelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var resultSet = mock(ResultSet.class);
        final String columnLabel = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.updateString(resultSet, columnLabel)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateString(ResultSet, String)
     * updateString(resultSet, columnLabel)} method invokes
     * {@link ResultSet#updateString(String, String) resultSet.updateString(columnLabel, string)}
     * with the {@code "hello, world"} string, and returns the {@code resultSet}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_hello_world_bytes(service());
        final var resultSet = mock(ResultSet.class);
        final var columnLabel = "label";
        // ------------------------------------------------------------------------------------ when
        final var result = service.updateString(resultSet, columnLabel);
        // ------------------------------------------------------------------------------------ then
        set_array12_invoked_once(service());
        verify(resultSet, times(1)).updateString(columnLabel, hello_world_string());
        assertSame(resultSet, result);
    }
}
