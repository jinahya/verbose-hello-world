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
 * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(PreparedStatement, int)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setCharacterStream(int, Reader)
 */
@DisplayName("setCharacterStream(statement, index)")
@Slf4j
class HelloWorld_SetCharacterStream_PreparedStatement_Int_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(PreparedStatement, int)
     * setCharacterStream(statement, index)} method throws a {@link NullPointerException} when the
     * {@code statement} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <statement> argument is <null>")
    @Test
    void _ThrowNullPointerException_PreparedStatementIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final PreparedStatement statement = null;
        final var index = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setCharacterStream(statement, index)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(PreparedStatement, int)
     * setCharacterStream(statement, index)} method throws an {@link IllegalArgumentException} when
     * the {@code index} argument is not positive.
     */
    @DisplayName("should throw an <IllegalArgumentException> when the <index> is not positive")
    @Test
    void _ThrowIllegalArgumentException_ParameterIndexIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var statement = Mockito.mock(PreparedStatement.class);
        final var index = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 1);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setCharacterStream(statement, index)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(PreparedStatement, int)
     * setCharacterStream(statement, index)} method invokes
     * {@link PreparedStatement#setCharacterStream(int, Reader) statement.setCharacterStream(index,
     * reader)} with a reader that yields the {@code "hello, world"} characters, and returns the
     * {@code statement}.
     */
    @DisplayName("""
            should invoke <statement.setCharacterStream(index, reader)>,
            and return the <statement>""")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.set_array_sets_hello_world_bytes(service());
        final var sink = new StringWriter();
        final var statement = Mockito.mock(PreparedStatement.class);
        Mockito.doAnswer(i -> {
            i.getArgument(1, Reader.class).transferTo(sink);
            return null;
        }).when(statement).setCharacterStream(
                ArgumentMatchers.intThat(v -> v >= 1),
                ArgumentMatchers.<Reader>notNull()
        );
        final var index = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setCharacterStream(statement, index);
        // ------------------------------------------------------------------------------------ then
        HelloWorld__TestUtils.set_array12_invoked_once(service);
        Mockito.verify(statement, Mockito.times(1))
                .setCharacterStream(Mockito.eq(index), Mockito.<Reader>notNull());
        Assertions.assertEquals(HelloWorld__TestUtils.hello_world_string(), sink.toString());
        Assertions.assertSame(statement, result);
    }
}
