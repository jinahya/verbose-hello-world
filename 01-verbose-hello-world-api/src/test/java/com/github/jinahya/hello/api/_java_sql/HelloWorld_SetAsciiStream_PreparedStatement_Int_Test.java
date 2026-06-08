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

import java.io.*;
import java.sql.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(PreparedStatement, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setAsciiStream(int, InputStream)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
 */
@DisplayName("setAsciiStream(statement, index)")
@Slf4j
class HelloWorld_SetAsciiStream_PreparedStatement_Int_Test extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(PreparedStatement, int)
     * setAsciiStream(statement, index)} method throws a {@link NullPointerException} when the
     * {@code statement} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <statement> argument is <null>")
    @Test
    void _ThrowNullPointerException_PreparedStatementIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final PreparedStatement statement = null;
        final var index = ThreadLocalRandom.current().nextInt() & 0x7FFFFFFF;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setAsciiStream(statement, index));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(PreparedStatement, int)
     * setAsciiStream(statement, index)} method throws an {@link IllegalArgumentException} when
     * the {@code index} argument is not positive.
     */
    @DisplayName("should throw an <IllegalArgumentException> when the <index> is not positive")
    @Test
    void _ThrowIllegalArgumentException_ParameterIndexIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var statement = mock(PreparedStatement.class);
        final var index = ThreadLocalRandom.current().nextInt() & 0x80000000;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setAsciiStream(statement, index)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(PreparedStatement, int)
     * setAsciiStream(statement, index)} method invokes
     * {@link PreparedStatement#setAsciiStream(int, InputStream) statement.setAsciiStream(index,
     * stream)} with a stream that yields the {@value HelloWorld#BYTES} bytes, and returns the
     * {@code statement}.
     */
    @DisplayName(
            "should invoke <statement.setAsciiStream(index, stream)>, and return the <statement>")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_random_bytes(service());
        final var sink = new ByteArrayOutputStream();
        final var statement = mock(PreparedStatement.class);
        doAnswer(i -> {
            final var x = i.getArgument(1, InputStream.class);
            x.transferTo(sink);
            return null;
        }).when(statement).setAsciiStream(intThat(v -> v >= 1), notNull());
        final var index = ThreadLocalRandom.current().nextInt() & 0x7FFFFFFF;
        // ------------------------------------------------------------------------------------ when
        final var result = service.setAsciiStream(statement, index);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(statement, times(1)).setAsciiStream(eq(index), notNull());
        assertArrayEquals(array, sink.toByteArray());
        assertSame(statement, result);
    }
}
