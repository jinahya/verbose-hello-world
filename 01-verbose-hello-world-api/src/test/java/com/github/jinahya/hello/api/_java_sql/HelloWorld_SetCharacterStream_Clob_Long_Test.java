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
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(Clob, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Clob#setCharacterStream(long)
 */
@DisplayName("setCharacterStream(clob, pos)")
@Slf4j
class HelloWorld_SetCharacterStream_Clob_Long_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(Clob, long)
     * setCharacterStream(clob, pos)} method throws a {@link NullPointerException} when the
     * {@code clob} argument is {@code null}.
     */
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
                () -> service.setCharacterStream(clob, pos)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(Clob, long)
     * setCharacterStream(clob, pos)} method throws an {@link IllegalArgumentException} when the
     * {@code pos} argument is not positive.
     */
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
                () -> service.setCharacterStream(clob, pos)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(Clob, long)
     * setCharacterStream(clob, pos)} method invokes
     * {@link com.github.jinahya.hello.api.HelloWorld#write(Writer) write(writer)} with the writer
     * obtained from {@link Clob#setCharacterStream(long) clob.setCharacterStream(pos)}, and returns
     * the {@code clob}.
     */
    @DisplayName("should invoke <write(clob.setCharacterStream(pos))>, and return the <clob>")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.write_writer_writes_hello_world_string(service());
        final var sink = new StringWriter();
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        Mockito.doReturn(sink).when(clob).setCharacterStream(pos);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setCharacterStream(clob, pos);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(clob, Mockito.times(1)).setCharacterStream(pos);
        Mockito.verify(service, Mockito.times(1)).write((Writer) sink);
        Assertions.assertEquals(HelloWorld__TestUtils.hello_world_string(), sink.toString());
        Assertions.assertSame(clob, result);
    }
}
