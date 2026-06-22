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
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob, pos)} method
 * with real {@link Clob} implementations from JDBC drivers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideFromPublishing
@DisplayName("HelloWorld.setString(Clob, long)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetString_Clob_Long__Test
        extends HelloWorld__Test {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var c = invocation.getArgument(0, Clob.class);
            final var p = invocation.getArgument(1, Long.class);
            c.setString(p, HELLO_WORLD_STRING);
            return c;
        }).when(service()).setString(
                ArgumentMatchers.<Clob>notNull(),
                ArgumentMatchers.longThat(v -> v >= 1L)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:setstring_clob_long_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob,
         * pos)} method populates a real H2 {@link Clob} with the {@code "hello, world"} string.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                assertEquals(0L, clob.length());
                final var pos = 1L;
                final var result = service().setString(clob, pos);
                assertSame(clob, result);
                assertEquals(HelloWorld.BYTES, clob.length());
                assertEquals(HELLO_WORLD_STRING, clob.getSubString(1L, HelloWorld.BYTES));
            }
        }
    }

    @DisplayName("HSQLDB")
    @Nested
    class Hsql_Test {

        private static final String URL = "jdbc:hsqldb:mem:setstring_clob_long_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob,
         * pos)} method populates a real HSQLDB {@link Clob} with the {@code "hello, world"}
         * string.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var pos = 1L;
                final var result = service().setString(clob, pos);
                assertSame(clob, result);
                assertEquals(HelloWorld.BYTES, clob.length());
                assertEquals("hello, world", clob.getSubString(1L, HelloWorld.BYTES));
            }
        }
    }

    @DisplayName("SQLite")
    @Disabled("SQLite JDBC driver does not implement Connection.createClob()")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setString(Clob, long) setString(clob,
         * pos)} method populates a real SQLite {@link Clob} with the {@code "hello, world"}
         * string.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var pos = 1L;
                final var result = service().setString(clob, pos);
                assertSame(clob, result);
                assertEquals(HelloWorld.BYTES, clob.length());
                assertEquals("hello, world", clob.getSubString(1L, HelloWorld.BYTES));
            }
        }
    }
}
