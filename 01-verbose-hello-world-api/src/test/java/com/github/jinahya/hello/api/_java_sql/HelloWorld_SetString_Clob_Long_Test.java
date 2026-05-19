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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Clob#setString(long, String)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
 */
@Slf4j
class HelloWorld_SetString_Clob_Long_Test
        extends HelloWorldTest {

    @DisplayName("(null, pos)NullPointerException")
    @Test
    void _ThrowNullPointerException_ClobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Clob clob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setString(clob, pos)
        );
    }

    @DisplayName("(clob, non-positive)IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setString(clob, pos)
        );
    }

    @DisplayName("clob.setString(pos, <string from set(byte[12])>)")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setString(clob, pos);
        // ------------------------------------------------------------------------------------ then
        HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(clob, Mockito.times(1))
                .setString(pos, HelloWorldTestUtils.hello_world_string());
        Assertions.assertSame(clob, result);
    }
}
