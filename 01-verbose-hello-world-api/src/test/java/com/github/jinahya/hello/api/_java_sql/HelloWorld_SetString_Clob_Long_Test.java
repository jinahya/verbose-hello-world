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

import java.sql.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Clob#setString(long, String)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
 */
@DisplayName("setString(clob, pos)")
@Slf4j
class HelloWorld_SetString_Clob_Long_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob, pos)}
     * method throws a {@link NullPointerException} when the {@code clob} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <clob> argument is <null>")
    @Test
    void _ThrowNullPointerException_ClobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Clob clob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setString(clob, pos));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob, pos)}
     * method throws an {@link IllegalArgumentException} when the {@code pos} argument is not
     * positive.
     */
    @DisplayName("should throw an <IllegalArgumentException> when the <pos> is not positive")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var clob = mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(IllegalArgumentException.class, () -> service.setString(clob, pos));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob, pos)}
     * method invokes {@link Clob#setString(long, String) clob.setString(pos, string)} with the
     * {@code "hello, world"} string, and returns the {@code clob}.
     */
    @DisplayName("should invoke <clob.setString(pos, string)>, and return the <clob>")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_hello_world_bytes(service());
        final var clob = mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setString(clob, pos);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(clob, times(1)).setString(pos, hello_world_string());
        assertSame(clob, result);
    }
}
