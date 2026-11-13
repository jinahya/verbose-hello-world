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

import java.sql.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.JdbcHelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link JdbcHelloWorld#updateString(ResultSet, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ResultSet#updateString(int, String)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
 */
@DisplayName("JdbcHelloWorld.updateString(ResultSet, int)")
@Slf4j
class JdbcHelloWorld_UpdateString_ResultSet_Int_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#updateString(ResultSet, int)
     * updateString(resultSet, columnIndex)} method throws a {@link NullPointerException} when the
     * {@code resultSet} argument is {@code null}.
     */
    @DisplayName("throws NPE / resultSet is null")
    @Test
    void _ThrowNullPointerException_ResultSetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final ResultSet resultSet = null;
        final var columnIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.updateString(resultSet, columnIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#updateString(ResultSet, int)
     * updateString(resultSet, columnIndex)} method throws an {@link IllegalArgumentException} when
     * the {@code columnIndex} argument is not positive.
     */
    @DisplayName("throws IAE / columnIndex is not positive")
    @Test
    void _ThrowIllegalArgumentException_ColumnIndexIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var resultSet = mock(ResultSet.class);
        final var columnIndex = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 1);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateString(resultSet, columnIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#updateString(ResultSet, int)
     * updateString(resultSet, columnIndex)} method invokes
     * {@link ResultSet#updateString(int, String) resultSet.updateString(columnIndex, string)} with
     * the {@code "hello, world"} string, and returns the {@code resultSet}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_index_sets_hello_world_bytes(service());
        final var resultSet = mock(ResultSet.class);
        final var columnIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.updateString(resultSet, columnIndex);
        // ------------------------------------------------------------------------------------ then
        set_array12_index0_invoked_once(service());
        verify(resultSet, times(1)).updateString(columnIndex, hello_world_string());
        assertSame(resultSet, result);
    }
}
