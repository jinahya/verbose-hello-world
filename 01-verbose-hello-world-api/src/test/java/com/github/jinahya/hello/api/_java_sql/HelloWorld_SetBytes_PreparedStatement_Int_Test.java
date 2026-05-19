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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(PreparedStatement, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setBytes(int, byte[])
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
 */
@Slf4j
class HelloWorld_SetBytes_PreparedStatement_Int_Test
        extends HelloWorldTest {

    @DisplayName("(null, parameterIndex)NullPointerException")
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

    @DisplayName("(preparedStatement, non-positive)IllegalArgumentException")
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

    @DisplayName("preparedStatement.setBytes(parameterIndex, set(byte[12]))")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var preparedStatement = Mockito.mock(PreparedStatement.class);
        final var parameterIndex = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBytes(preparedStatement, parameterIndex);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(preparedStatement, Mockito.times(1)).setBytes(parameterIndex, array);
        Assertions.assertSame(preparedStatement, result);
    }
}
