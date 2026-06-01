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
import java.util.concurrent.*;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(Clob, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Clob#setAsciiStream(long)
 */
@DisplayName("setAsciiStream(clob, pos)")
@Slf4j
class HelloWorld_SetAsciiStream_Clob_Long_Test
        extends HelloWorld__Test {

    @DisplayName("should throw a <NullPointerException> when the <clob> argument is <null>")
    @Test
    void _ThrowNullPointerException_ClobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Clob clob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setAsciiStream(clob, pos)
        );
    }

    @DisplayName("should throw an <IllegalArgumentException> when the <pos> is not positive")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setAsciiStream(clob, pos)
        );
    }

    @DisplayName("should invoke <write(clob.setAsciiStream(pos))>, and return the <clob>")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.write_outputstream_writes_hello_world_bytes(
                service());
        final var sink = new ByteArrayOutputStream();
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        Mockito.doReturn(sink).when(clob).setAsciiStream(pos);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setAsciiStream(clob, pos);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(clob, Mockito.times(1)).setAsciiStream(pos);
        Mockito.verify(service, Mockito.times(1)).write((OutputStream) sink);
        Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                     sink.toByteArray());
        Assertions.assertSame(clob, result);
    }
}
