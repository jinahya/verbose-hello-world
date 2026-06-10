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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)} method
 * with real {@link Blob} implementations from JDBC drivers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("setBytes(blob, pos)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetBytes_Blob_Long__Test
        extends HelloWorld__Test {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var blob = invocation.getArgument(0, Blob.class);
            final var pos = invocation.getArgument(1, Long.class);
            blob.setBytes(pos, hello_world_byte_array());
            return blob;
        }).when(service()).setBytes(
                ArgumentMatchers.<Blob>notNull(),
                ArgumentMatchers.longThat(v -> v >= 1L)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Nested
    class H2_Test {

        private static final String URL = "jdbc:h2:mem:setbytes_blob_long_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)}
         * method populates a real H2 {@link Blob} with the {@code "hello, world"} bytes.
         */
        @DisplayName("should populate a <real H2 Blob> through <setBytes(blob, 1L)>")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var blob = connection.createBlob();
                final var result = service().setBytes(blob, 1L);
                assertSame(blob, result);
                assertEquals(HelloWorld.BYTES, blob.length());
                assertArrayEquals(hello_world_byte_array(), blob.getBytes(1L, HelloWorld.BYTES));
            }
        }
    }

    @DisplayName("HSQLDB")
    @Nested
    class Hsql_Test {

        private static final String URL = "jdbc:hsqldb:mem:setbytes_blob_long_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)}
         * method populates a real HSQLDB {@link Blob} with the {@code "hello, world"} bytes.
         */
        @DisplayName("should populate a <real HSQLDB Blob> through <setBytes(blob, 1L)>")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var blob = connection.createBlob();
                final var result = service().setBytes(blob, 1L);
                assertSame(blob, result);
                assertEquals(HelloWorld.BYTES, blob.length());
                assertArrayEquals(
                        hello_world_byte_array(),
                        blob.getBytes(1L, HelloWorld.BYTES)
                );
            }
        }
    }

    @DisplayName("SQLite")
    @Disabled("SQLite JDBC driver does not implement Connection.createBlob()")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long) setBytes(blob, pos)}
         * method populates a real SQLite {@link Blob} with the {@code "hello, world"} bytes.
         */
        @DisplayName("should populate a <real SQLite Blob> through <setBytes(blob, 1L)>")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var blob = connection.createBlob();
                final var result = service().setBytes(blob, 1L);
                assertSame(blob, result);
            }
        }
    }
}
