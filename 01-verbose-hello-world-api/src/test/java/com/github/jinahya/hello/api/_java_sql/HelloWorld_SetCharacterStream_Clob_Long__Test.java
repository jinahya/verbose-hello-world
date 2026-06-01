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

import java.io.*;
import java.sql.*;

@DisplayName("setCharacterStream(clob, pos)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetCharacterStream_Clob_Long__Test
        extends HelloWorld__Test {

    @BeforeEach
    void __() throws IOException, SQLException {
        Mockito.doAnswer(invocation -> {
            final var clob = invocation.getArgument(0, Clob.class);
            final var pos = invocation.getArgument(1, Long.class);
            clob.setString(pos, HelloWorld__TestConstants.HELLO_WORLD_STRING);
            return clob;
        }).when(service()).setCharacterStream(
                ArgumentMatchers.<Clob>notNull(),
                ArgumentMatchers.longThat(v -> v >= 1L)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:setcharacterstream_clob_long_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        @DisplayName("should populate a <real H2 Clob> through <setCharacterStream(clob, 1L)>")
        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var result = service().setCharacterStream(clob, 1L);
                Assertions.assertSame(clob, result);
                Assertions.assertEquals(HelloWorld.BYTES, clob.length());
                Assertions.assertEquals(
                        HelloWorld__TestConstants.HELLO_WORLD_STRING,
                        clob.getSubString(1L, HelloWorld.BYTES)
                );
            }
        }
    }

    @DisplayName("HSQLDB")
    @Nested
    class Hsql_Test {

        private static final String URL = "jdbc:hsqldb:mem:setcharacterstream_clob_long_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        @DisplayName("should populate a <real HSQLDB Clob> through <setCharacterStream(clob, 1L)>")
        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var result = service().setCharacterStream(clob, 1L);
                Assertions.assertSame(clob, result);
                Assertions.assertEquals(HelloWorld.BYTES, clob.length());
                Assertions.assertEquals(
                        HelloWorld__TestConstants.HELLO_WORLD_STRING,
                        clob.getSubString(1L, HelloWorld.BYTES)
                );
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

        @DisplayName("should populate a <real SQLite Clob> through <setCharacterStream(clob, 1L)>")
        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var result = service().setCharacterStream(clob, 1L);
                Assertions.assertSame(clob, result);
            }
        }
    }
}
