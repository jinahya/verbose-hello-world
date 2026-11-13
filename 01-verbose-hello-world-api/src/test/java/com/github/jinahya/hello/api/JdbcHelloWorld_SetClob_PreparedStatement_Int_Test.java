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
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.JdbcHelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link JdbcHelloWorld#setClob(PreparedStatement, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setClob(int, Reader)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
 */
@DisplayName("JdbcHelloWorld.setClob(PreparedStatement, int)")
@Slf4j
class JdbcHelloWorld_SetClob_PreparedStatement_Int_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setClob(PreparedStatement, int)
     * setClob(preparedStatement, parameterIndex)} method throws a {@link NullPointerException} when
     * the {@code preparedStatement} argument is {@code null}.
     */
    @DisplayName("throws NPE / preparedStatement is null")
    @Test
    void _ThrowNullPointerException_PreparedStatementIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final PreparedStatement preparedStatement = null;
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> service.setClob(preparedStatement, parameterIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setClob(PreparedStatement, int)
     * setClob(preparedStatement, parameterIndex)} method throws an {@link IllegalArgumentException}
     * when the {@code parameterIndex} argument is not positive.
     */
    @DisplayName("throws IAE / parameterIndex is not positive")
    @Test
    void _ThrowIllegalArgumentException_ParameterIndexIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var preparedStatement = mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 1);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setClob(preparedStatement, parameterIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setClob(PreparedStatement, int)
     * setClob(preparedStatement, parameterIndex)} method invokes
     * {@link PreparedStatement#setClob(int, Reader) preparedStatement.setClob(parameterIndex,
     * reader)} with a reader, which is still readable after the method returns, that yields the
     * {@code "hello, world"} characters, and returns the {@code preparedStatement}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_index_sets_hello_world_bytes(service());
        final var preparedStatement = mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setClob(preparedStatement, parameterIndex);
        // ------------------------------------------------------------------------------------ then
        set_array12_index0_invoked_once(service());
        final var captor = ArgumentCaptor.forClass(Reader.class);
        verify(preparedStatement, times(1)).setClob(eq(parameterIndex), captor.capture());
        final var sink = new StringWriter();
        captor.getValue().transferTo(sink);
        assertEquals(hello_world_string(), sink.toString());
        assertSame(preparedStatement, result);
    }
}
