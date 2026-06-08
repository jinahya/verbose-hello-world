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

import java.sql.*;
import java.util.concurrent.*;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Blob#setBytes(long, byte[])
 */
@DisplayName("setBytes(blob, pos)")
@Slf4j
class HelloWorld_SetBytes_Blob_Long_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)}
     * method throws a {@link NullPointerException} when the {@code blob} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <blob> argument is <null>")
    @Test
    void _ThrowNullPointerException_BlobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Blob blob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setBytes(blob, pos)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)}
     * method throws an {@link IllegalArgumentException} when the {@code pos} argument is not
     * positive.
     */
    @DisplayName("should throw an <IllegalArgumentException> when the <pos> is not positive")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var blob = Mockito.mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setBytes(blob, pos)
        );
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)}
     * method invokes {@link Blob#setBytes(long, byte[]) blob.setBytes(pos, array)} with the array
     * filled by {@link com.github.jinahya.hello.api.HelloWorld#set(byte[]) set(array)}, and returns
     * the {@code blob}.
     */
    @DisplayName("should invoke <blob.setBytes(pos, buffer)>, and return the <blob>")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.set_array_returns_the_array(service());
        final var blob = Mockito.mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBytes(blob, pos);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorld__TestUtils.set_array12_invoked_once(service);
        Mockito.verify(blob, Mockito.times(1)).setBytes(pos, array);
        Assertions.assertSame(blob, result);
    }
}
