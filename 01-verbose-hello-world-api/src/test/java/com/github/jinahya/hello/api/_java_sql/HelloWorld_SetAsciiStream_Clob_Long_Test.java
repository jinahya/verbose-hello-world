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
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(Clob, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Clob#setAsciiStream(long)
 */
@DisplayName("HelloWorld.setAsciiStream(Clob, long)")
@Slf4j
class HelloWorld_SetAsciiStream_Clob_Long_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(Clob, long)
     * setAsciiStream(clob, pos)} method throws a {@link NullPointerException} when the {@code clob}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / clob is null")
    @Test
    void _ThrowNullPointerException_ClobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Clob clob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setAsciiStream(clob, pos));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(Clob, long)
     * setAsciiStream(clob, pos)} method throws an {@link IllegalArgumentException} when the
     * {@code pos} argument is not positive.
     */
    @DisplayName("throws IAE / pos is not positive")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var clob = mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setAsciiStream(clob, pos)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(Clob, long)
     * setAsciiStream(clob, pos)} method invokes
     * {@link com.github.jinahya.hello.api.HelloWorld#write(OutputStream) write(stream)} with the
     * stream obtained from {@link Clob#setAsciiStream(long) clob.setAsciiStream(pos)}, and returns
     * the {@code clob}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = write_outputstream_writes_hello_world_bytes(service());
        final var sink = new ByteArrayOutputStream();
        final var clob = mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        doReturn(sink).when(clob).setAsciiStream(pos);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setAsciiStream(clob, pos);
        // ------------------------------------------------------------------------------------ then
        verify(clob, times(1)).setAsciiStream(pos);
        verify(service, times(1)).write((OutputStream) sink);
        assertArrayEquals(hello_world_byte_array(), sink.toByteArray());
        assertSame(clob, result);
    }
}
