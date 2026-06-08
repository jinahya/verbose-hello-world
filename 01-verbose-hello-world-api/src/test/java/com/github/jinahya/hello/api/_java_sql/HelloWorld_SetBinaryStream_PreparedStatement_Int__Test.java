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

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#setBinaryStream(PreparedStatement, int)
 * setBinaryStream(statement, index)} method with real {@link PreparedStatement} implementations
 * from JDBC drivers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("setBinaryStream(statement, index)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetBinaryStream_PreparedStatement_Int__Test
        extends HelloWorld__Test {

    @BeforeEach
    void __() throws IOException, SQLException {
        Mockito.doAnswer(invocation -> {
            final var ps = invocation.getArgument(0, PreparedStatement.class);
            final var pi = invocation.getArgument(1, Integer.class);
            ps.setBinaryStream(
                    pi,
                    new ByteArrayInputStream(HelloWorld__TestUtils.hello_world_byte_array())
            );
            return ps;
        }).when(service()).setBinaryStream(
                ArgumentMatchers.<PreparedStatement>notNull(),
                ArgumentMatchers.intThat(v -> v >= 1)
        );
        HelloWorld__TestUtils.set_array_returns_the_array(service());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:setbinarystream_preparedstatement_int_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setBinaryStream(PreparedStatement, int)
         * setBinaryStream(statement, index)} method inserts and reads back the
         * {@code "hello, world"} bytes through a real H2 {@link PreparedStatement}.
         */
        @DisplayName("""
                should insert and read back the hello-world bytes
                through a <real H2 PreparedStatement>""")
        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                %s VARBINARY(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (%s) VALUES (?)".formatted(TABLE, COLUMN);
                    try (var statement = connection.prepareStatement(sql)) {
                        service().setBinaryStream(statement, 1);
                        final var result = statement.executeUpdate();
                        assert result == 1;
                    }
                }
                {
                    final var sql = "SELECT * FROM %s".formatted(TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        Assertions.assertTrue(resultSet.next());
                        try (var stream = resultSet.getBinaryStream(COLUMN)) {
                            Assertions.assertNotNull(stream);
                            final var bytes = stream.readAllBytes();
                            Assertions.assertEquals(HelloWorld.BYTES, bytes.length);
                            Assertions.assertArrayEquals(
                                    HelloWorld__TestUtils.hello_world_byte_array(),
                                    bytes
                            );
                        }
                    }
                }
            }
        }
    }

    @DisplayName("HSQLDB")
    @Nested
    class Hsql_Test {

        private static final String URL
                = "jdbc:hsqldb:mem:setbinarystream_preparedstatement_int_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setBinaryStream(PreparedStatement, int)
         * setBinaryStream(statement, index)} method inserts and reads back the
         * {@code "hello, world"} bytes through a real HSQLDB {@link PreparedStatement}.
         */
        @DisplayName("""
                should insert and read back the hello-world bytes
                through a <real HSQLDB PreparedStatement>""")
        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                %s VARBINARY(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (%s) VALUES (?)".formatted(TABLE, COLUMN);
                    try (var statement = connection.prepareStatement(sql)) {
                        service().setBinaryStream(statement, 1);
                        final var result = statement.executeUpdate();
                        assert result == 1;
                    }
                }
                {
                    final var sql = "SELECT * FROM %s".formatted(TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        Assertions.assertTrue(resultSet.next());
                        try (var stream = resultSet.getBinaryStream(COLUMN)) {
                            Assertions.assertNotNull(stream);
                            final var bytes = stream.readAllBytes();
                            Assertions.assertEquals(HelloWorld.BYTES, bytes.length);
                            Assertions.assertArrayEquals(
                                    HelloWorld__TestUtils.hello_world_byte_array(),
                                    bytes
                            );
                        }
                    }
                }
            }
        }
    }

    @DisplayName("SQLite")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.HelloWorld#setBinaryStream(PreparedStatement, int)
         * setBinaryStream(statement, index)} method inserts and reads back the
         * {@code "hello, world"} bytes through a real SQLite {@link PreparedStatement}.
         */
        @DisplayName("""
                should insert and read back the hello-world bytes
                through a <real SQLite PreparedStatement>""")
        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                %s VARBINARY(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (%s) VALUES (?)".formatted(TABLE, COLUMN);
                    try (var statement = connection.prepareStatement(sql)) {
                        service().setBinaryStream(statement, 1);
                        final var result = statement.executeUpdate();
                        assert result == 1;
                    }
                }
                {
                    final var sql = "SELECT * FROM %s".formatted(TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        Assertions.assertTrue(resultSet.next());
                        try (var stream = resultSet.getBinaryStream(COLUMN)) {
                            Assertions.assertNotNull(stream);
                            final var bytes = stream.readAllBytes();
                            Assertions.assertEquals(HelloWorld.BYTES, bytes.length);
                            Assertions.assertArrayEquals(
                                    HelloWorld__TestUtils.hello_world_byte_array(),
                                    bytes
                            );
                        }
                    }
                }
            }
        }
    }
}
