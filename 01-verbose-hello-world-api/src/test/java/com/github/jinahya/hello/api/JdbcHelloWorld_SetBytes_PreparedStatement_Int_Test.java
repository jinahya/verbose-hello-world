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
 * {@link JdbcHelloWorld#setBytes(PreparedStatement, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setBytes(int, byte[])
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
 */
@DisplayName("JdbcHelloWorld.setBytes(PreparedStatement, int)")
@Slf4j
class JdbcHelloWorld_SetBytes_PreparedStatement_Int_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setBytes(PreparedStatement, int)
     * setBytes(statement, index)} method throws a {@link NullPointerException} when the
     * {@code statement} argument is {@code null}.
     */
    @DisplayName("throws NPE / statement is null")
    @Test
    void _ThrowNullPointerException_PreparedStatementIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final PreparedStatement preparedStatement = null;
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.setBytes(preparedStatement, parameterIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setBytes(PreparedStatement, int)
     * setBytes(statement, index)} method throws an {@link IllegalArgumentException} when the
     * {@code index} argument is not positive.
     */
    @DisplayName("throws IAE / index is not positive")
    @Test
    void _ThrowIllegalArgumentException_ParameterIndexIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var preparedStatement = mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 1);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setBytes(preparedStatement, parameterIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setBytes(PreparedStatement, int)
     * setBytes(statement, index)} method invokes
     * {@link PreparedStatement#setBytes(int, byte[]) statement.setBytes(index, array)} with the
     * array filled by {@link JdbcHelloWorld#set(byte[], int) set(array, 0)}, and
     * returns the {@code statement}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_index_returns_the_array(service());
        final var preparedStatement = mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBytes(preparedStatement, parameterIndex);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_index0_invoked_once(service());
        verify(preparedStatement, times(1)).setBytes(parameterIndex, array);
        assertSame(preparedStatement, result);
    }
}
