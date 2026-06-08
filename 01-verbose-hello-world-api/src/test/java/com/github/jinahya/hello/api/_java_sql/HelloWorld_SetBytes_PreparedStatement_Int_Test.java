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

import java.sql.*;
import java.util.concurrent.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(PreparedStatement, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setBytes(int, byte[])
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
 */
@DisplayName("setBytes(statement, index)")
@Slf4j
class HelloWorld_SetBytes_PreparedStatement_Int_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(PreparedStatement, int)
     * setBytes(statement, index)} method throws a {@link NullPointerException} when the
     * {@code statement} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <statement> argument is <null>")
    @Test
    void _ThrowNullPointerException_PreparedStatementIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final PreparedStatement preparedStatement = null;
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setBytes(preparedStatement, parameterIndex)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(PreparedStatement, int)
     * setBytes(statement, index)} method throws an {@link IllegalArgumentException} when the
     * {@code index} argument is not positive.
     */
    @DisplayName("should throw an <IllegalArgumentException> when the <index> is not positive")
    @Test
    void _ThrowIllegalArgumentException_ParameterIndexIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var preparedStatement = Mockito.mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 1);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setBytes(preparedStatement, parameterIndex)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(PreparedStatement, int)
     * setBytes(statement, index)} method invokes
     * {@link PreparedStatement#setBytes(int, byte[]) statement.setBytes(index, array)} with the
     * array filled by {@link com.github.jinahya.hello.api.HelloWorld#set(byte[]) set(array)}, and
     * returns the {@code statement}.
     */
    @DisplayName("should invoke <statement.setBytes(index, buffer)>, and return the <statement>")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.set_array_returns_the_array(service());
        final var preparedStatement = Mockito.mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBytes(preparedStatement, parameterIndex);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorld__TestUtils.set_array12_invoked_once(service);
        Mockito.verify(preparedStatement, Mockito.times(1)).setBytes(parameterIndex, array);
        Assertions.assertSame(preparedStatement, result);
    }
}
