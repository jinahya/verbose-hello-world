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

import java.io.*;
import java.sql.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.JdbcHelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link JdbcHelloWorld#setBinaryStream(Blob, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Blob#setBinaryStream(long)
 */
@DisplayName("JdbcHelloWorld.setBinaryStream(Blob, long)")
@Slf4j
class JdbcHelloWorld_SetBinaryStream_Blob_Long_Test
        extends JdbcHelloWorld__Test {

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setBinaryStream(Blob, long)
     * setBinaryStream(blob, pos)} method throws a {@link NullPointerException} when the
     * {@code blob} argument is {@code null}.
     */
    @DisplayName("throws NPE / blob is null")
    @Test
    void _ThrowNullPointerException_BlobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Blob blob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setBinaryStream(blob, pos));
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setBinaryStream(Blob, long)
     * setBinaryStream(blob, pos)} method throws an {@link IllegalArgumentException} when the
     * {@code pos} argument is not positive.
     */
    @DisplayName("throws IAE / pos is not positive")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var blob = mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setBinaryStream(blob, pos)
        );
    }

    /**
     * Verifies that the
     * {@link JdbcHelloWorld#setBinaryStream(Blob, long)
     * setBinaryStream(blob, pos)} method writes the
     * hello-world-bytes, set by {@link JdbcHelloWorld#set(byte[], int) set(array, 0)}, to the
     * stream obtained from {@link Blob#setBinaryStream(long) blob.setBinaryStream(pos)}, and
     * returns the {@code blob}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_index_sets_hello_world_bytes(service());
        final var sink = new ByteArrayOutputStream();
        final var blob = mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        doReturn(sink).when(blob).setBinaryStream(pos);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBinaryStream(blob, pos);
        // ------------------------------------------------------------------------------------ then
        verify(blob, times(1)).setBinaryStream(pos);
        assertArrayEquals(hello_world_byte_array(), sink.toByteArray());
        assertSame(blob, result);
    }
}
