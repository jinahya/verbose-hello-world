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
 * {@link JdbcHelloWorld#updateBlob(ResultSet, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ResultSet#updateBlob(int, InputStream)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
 */
@DisplayName("JdbcHelloWorld.updateBlob(ResultSet, int)")
@Slf4j
class JdbcHelloWorld_UpdateBlob_ResultSet_Int_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#updateBlob(ResultSet, int)
     * updateBlob(resultSet, columnIndex)} method throws a {@link NullPointerException} when the
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
        assertThrows(NullPointerException.class, () -> service.updateBlob(resultSet, columnIndex));
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#updateBlob(ResultSet, int)
     * updateBlob(resultSet, columnIndex)} method throws an {@link IllegalArgumentException} when
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
                () -> service.updateBlob(resultSet, columnIndex)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#updateBlob(ResultSet, int)
     * updateBlob(resultSet, columnIndex)} method invokes
     * {@link ResultSet#updateBlob(int, InputStream) resultSet.updateBlob(columnIndex, stream)} with
     * a stream, which is still readable after the method returns, that yields the
     * {@code "hello, world"} bytes, and returns the {@code resultSet}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_index_sets_hello_world_bytes(service());
        final var resultSet = mock(ResultSet.class);
        final var columnIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.updateBlob(resultSet, columnIndex);
        // ------------------------------------------------------------------------------------ then
        set_array12_index0_invoked_once(service());
        final var captor = ArgumentCaptor.forClass(InputStream.class);
        verify(resultSet, times(1)).updateBlob(eq(columnIndex), captor.capture());
        final var sink = new ByteArrayOutputStream();
        captor.getValue().transferTo(sink);
        assertArrayEquals(hello_world_byte_array(), sink.toByteArray());
        assertSame(resultSet, result);
    }
}
