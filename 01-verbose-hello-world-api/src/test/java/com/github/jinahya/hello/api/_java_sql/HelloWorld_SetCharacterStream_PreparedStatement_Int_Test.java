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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setCharacterStream(PreparedStatement, int)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PreparedStatement#setCharacterStream(int, Reader)
 */
@Slf4j
class HelloWorld_SetCharacterStream_PreparedStatement_Int_Test
        extends HelloWorldTest {

    @DisplayName("(null, parameterIndex)NullPointerException")
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

    @DisplayName("(preparedStatement, non-positive)IllegalArgumentException")
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

    @DisplayName("preparedStatement.setCharacterStream(parameterIndex, <reader of set(byte[12])>)")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
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
        HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(statement, Mockito.times(1))
                .setCharacterStream(Mockito.eq(index), Mockito.<Reader>notNull());
        Assertions.assertEquals(HelloWorldTestUtils.hello_world_string(), sink.toString());
        Assertions.assertSame(statement, result);
    }
}
